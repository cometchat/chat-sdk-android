# Chat SDK CI/CD Workflow Guide (v5)

CI/CD automation for the CometChat Android Chat SDK. **Unit tests only — no instrumented /
emulator tests in the v5 pipeline.** Merges into `dev-v5` cut an internal release to Cloudsmith
`call-team`; merges into `master-v5` cut the public GA release.

---

## Branch Strategy

```
feature/* ──PR──▶ release-v5-* ──PR──▶ dev-v5 ──PR──▶ master-v5
                                    (internal release)   (public GA release)
                                     Cloudsmith           Cloudsmith
                                     call-team            cometchat/cometchat
```

`master-v5` only ever receives PRs from `dev-v5` — never directly from `release-v5-*`.



| Branch | Purpose |
|--------|---------|
| `feature/*` / `ENG-*` | Individual feature/bugfix branches |
| `release-v5-*` | Release candidate branches |
| `dev-v5` | Merge → internal release (Cloudsmith `call-team`) + GitHub release on this repo |
| `master-v5` | Merge → public GA release (dependencies, mirror, Cloudsmith production, public GitHub release). Source: `dev-v5` only. |
| `v5-automation-tests` | **CI sandbox** (same pattern as uikit's `v6-automation-tests`) — PRs merged into it run the full internal-release flow without touching `dev-v5`. Releases are marked pre-release. |

The base version lives in `chat-sdk-android/build.gradle`
(`def libraryVersion = System.getenv("LIBRARY_VERSION") ?: '5.0.5'`).
**Internal (dev-v5) releases** publish `<base>-<epoch_seconds>` — auto-generated, unique per
build, no bump needed. **GA (master-v5) releases** publish the base verbatim — bump it in the
release PR.



---

## Workflow Files

| File | Trigger | Purpose |
|------|---------|---------|
| `test-sdk-unit.yml` | PR opened/updated → `release-v5-*`, `dev-v5` or `v5-automation-tests` | Unit tests (JUnit 5 + JUnit 4 via vintage engine) |
| `publish-dev-v5.yml` | PR **merged** → `dev-v5` or `v5-automation-tests` | Internal release: unit tests → Cloudsmith `call-team` → GitHub release |
| `release-v5.yml` | PR **merged** → `master-v5` | Public GA release: dependencies → mirror → Cloudsmith production → public GitHub release |

> `android_instrumentation_tests.yml` is the legacy v3/v4 emulator workflow (triggers on
> `master-v3`/`master-v4` only) and is untouched by the v5 flow. **Do not add instrumented
> tests to the v5 pipeline.**

---

## Flow 1: PR into `release-v5-*` or `dev-v5` (pre-merge)

`test-sdk-unit.yml` runs `./gradlew :chat-sdk-android:testDebugUnitTest` (ubuntu-latest, JDK 11)
and uploads the reports (14-day retention). Require the **"SDK Unit Tests / unit-tests"** check
in the branch protection rules so a red run blocks the merge.

## Flow 2: PR merged into `dev-v5` — internal release

`publish-dev-v5.yml`, using **Hritika's Cloudsmith key**:

1. **Unit tests** — the gate; nothing below runs if they fail.
2. **Cloudsmith internal release**
   - (a) credentials: `cloudsmith.apikey=$CLOUDSMITH_API_KEY_HRITIKA` written to `local.properties`
   - version: `<base>-<epoch_seconds>` (e.g. `5.0.5-1752780000`) — auto-generated, unique
     per build, passed to Gradle via `LIBRARY_VERSION`
   - (b) build the AAR: `assembleDistributable` (minified; regenerates `proguard_mapping.txt`)
   - (c) repo: `cometchat/call-team`
   - (d) publish: `publishMavenPublicationToCloudsmithRepository`
3. **GitHub release** on `cometchat-team/chat-sdk-android`
   - tag + name: **`v<base>`** — clean, **no epoch** (e.g. `v5.0.5`). Only Cloudsmith
     call-team carries the `-<epoch>` suffix. Repeated dev-v5 merges of the same base
     update the same `v<base>` release.
   - target branch `dev-v5`
   - notes/changelog: the merged PR's description, verbatim

### Testing the internal-release flow (sandbox)

`v5-automation-tests` mirrors uikit's `v6-automation-tests`. To test CI changes end to end
without touching `dev-v5`:

1. `git checkout dev-v5 && git checkout -b v5-automation-tests && git push -u origin v5-automation-tests`
2. Open a small PR into `v5-automation-tests` (put text in the description — it becomes the
   release notes) and merge it.
3. The full flow runs: unit tests → `<base>-<epoch>` published to call-team → GitHub release
   tagged on `v5-automation-tests` (marked **pre-release** so it's obviously not a real one).
4. Clean up: delete the release + tag; delete or keep the branch for next time. Sandbox
   versions on call-team are harmless (internal repo, unique epoch versions).

Requires only the `CLOUDSMITH_API_KEY_HRITIKA` secret. The GA flow has its own separate sandbox,
`v5-ga-tests` (below), which redirects every destination to safe targets.

### Testing the GA flow (staging — `v5-ga-tests`)

Merging a PR into **`v5-ga-tests`** runs `release-v5.yml` end to end against safe targets. The
steps are the same, only the destinations differ — and the deps flow becomes a **GPG-signing
smoke test** against card-renderer (staging deliberately exercises the signed commit + merge +
tag path that broke in production, since the real deps merges can't be run against production):

| | Live (`master-v5`) | Staging (`v5-ga-tests`) |
|---|---|---|
| Version | exact base, e.g. `5.0.5` | `5.0.5-staging.<epoch>` (unique per run) |
| Deps repo | `cometchat-team/dependencies-android` | **`ashfaqcometchat/card-renderer`** (dev-v5/master-v5 created off its default branch if missing) |
| Deps commit | bump `versionName` in `cometchat-pro-android-dependencies/build.gradle` | write `RELEASE_VERSION.txt` marker (card-renderer isn't the deps project) |
| Deps git flow | branch `release-v5-<version>` → **signed** commit → **signed** merge dev-v5 → **signed** merge master-v5 | **identical**, plus a **signed tag** `v<version>` — this is the whole point of the staging test |
| Deps AAR publish | `chat-sdk-android-dependencies:<version>` → `cometchat/cometchat` | **skipped** (nothing to publish from card-renderer) |
| SDK AAR | `chat-sdk-android:<version>` → `cometchat/cometchat` | → `cometchat/call-team` |
| Mirror target | `cometchat/chat-sdk-android` @ `v5` | `ashfaqcometchat/card-renderer` @ `chat-sdk-citest` |
| GitHub release | public repo, normal | card-renderer, marked **pre-release** |

**Verifying GPG signing after a staging run** — on `card-renderer`: the new merge commits on
`dev-v5`/`master-v5` and the tag `v<version>` should all show a green **"Verified"** badge. If
they do, signing is proven and a real GA into the protected `dependencies-android` will pass.
(Note: card-renderer's dev-v5/master-v5 are not protected, so this proves the signing
*mechanism* — to also test the *protection gate*, enable "Require signed commits" on
card-renderer's `dev-v5`.)

Same secrets as live (Ashfaq's PAT must have write on `card-renderer` — it's his repo).
Setup: `git checkout dev-v5 && git checkout -b v5-ga-tests && git push -u origin v5-ga-tests`,
then open a PR into it and merge. Clean up staging branches/tags/releases on card-renderer
afterwards.

## Flow 3: PR merged into `master-v5` — public GA release

`release-v5.yml`, using **Ashfaq's credentials** throughout:

1. **dependencies-android publish** (`cometchat-team/dependencies-android`) — all commits/merges
   **GPG-signed** (a `Set up GPG signing` step configures signing globally; dev-v5/master-v5
   require verified signatures)
   - clone the repo
   - create branch `release-v5-<version>` off `dev-v5`; bump `versionName` in
     `cometchat-pro-android-dependencies/build.gradle` (version taken from the SDK's
     `build.gradle`); **signed** commit + push
   - **signed** merge `release-v5-<version>` → `dev-v5` (direct merge + push)
   - checkout `master-v5`, **signed** merge `dev-v5` → `master-v5`, push
   - `./gradlew clean` then `./gradlew publish` (that project publishes to
     `cometchat/cometchat` — the URL is hardcoded in its own build.gradle)
2. **Mirror to the public repo** (`cometchat/chat-sdk-android`, branch `v5`) — copies everything
   from the private repo except `README.md`, including the distribution folder.
   `continue-on-error: true`, so a failure reports but does not stop the release.
   **⚠️ See "Mirror scope" below.**
3. **Cloudsmith production release** — `CLOUDSMITH_REPO=cometchat/cometchat`, then
   `clean` → `assembleDistributable` → `publishMavenPublicationToCloudsmithRepository`
4. **GitHub release on the public repo** — tag `v<version>`, target branch `v5`, notes from the
   PR description

### ⚠️ Mirror scope

The mirror step copies **every tracked file except `README.md`** from the private repo into the
public one, per spec. Be aware of what that means today:

- The private repo contains the SDK **source** (`chat-sdk-android/src`, ~179 `.java` files,
  `reference/`, build files).
- The public repo has **never** contained source — only Maven artifacts (`<version>/*.aar|*.pom`
  + checksums, `maven-metadata.xml`) plus `README.md`/`SECURITY.md`. Every past release commit
  touched only artifact folders and metadata.

So the first run of this step publishes the closed-source SDK's source publicly, and that is not
reversible (forks, clones, caches, search indexes). If the intent is artifact-only publishing —
matching the public repo's existing layout — replace the `rsync` in the "Mirror private repo to
public repo" step with:

```bash
cp -r chat-sdk-android/distribution/com/cometchat/chat-sdk-android/<version> /tmp/public-repo/
cp chat-sdk-android/distribution/com/cometchat/chat-sdk-android/maven-metadata.xml* /tmp/public-repo/
```

The step also **overwrites** files rather than deleting the public repo's existing content, so
previously published `<version>/` folders and `maven-metadata.xml` survive — deleting them would
break customers resolving those versions from the repo.

---

## Gradle wiring (chat-sdk-android/build.gradle)

- `def libraryVersion = System.getenv("LIBRARY_VERSION") ?: '<base>'` — the only place a
  version is written by hand. It feeds `versionName`, the Maven publication version, and
  `BuildConfig.VERSION_NAME`. The dev flow overrides it per build with `<base>-<epoch>`;
  the GA flow publishes the base verbatim (no override).
- `CLOUDSMITH_REPO` env var selects the Cloudsmith repo. Default: `cometchat/call-team`.
- The `cloudsmith` Maven repo reads `cloudsmith.username` / `cloudsmith.apikey` from
  `local.properties` (guarded — a missing file only fails at publish time, not on every build).
- `junit-vintage-engine` is required at test runtime: `useJUnitPlatform()` is enabled and most
  unit tests are JUnit 4 — without vintage they are **silently skipped** (312 tests should run).

## Secrets Required (repo → Settings → Secrets → Actions)

| Secret | Used In | Purpose |
|--------|---------|---------|
| `CLOUDSMITH_API_KEY_HRITIKA` | `publish-dev-v5.yml` | **Hritika's** Cloudsmith key — internal `call-team` releases |
| `CLOUDSMITH_API_KEY_ASHFAQ` | `release-v5.yml` | **Ashfaq's** Cloudsmith key — production `cometchat/cometchat` releases |
| `PUBLIC_REPO_TOKEN_ASHFAQ` | `release-v5.yml` | Ashfaq's GitHub PAT — write access to `cometchat-team/dependencies-android` **and** public `cometchat/chat-sdk-android` |
| `GPG_PRIVATE_KEY` + `GPG_PASSPHRASE` | `release-v5.yml` | Signed mirror commits (same keys/identity as the uikit repo). **Required** — the public repo only accepts signed commits; the mirror step aborts if these are missing. |
| `GITHUB_TOKEN` (built-in) | `publish-dev-v5.yml` | GitHub release on this repo — no setup needed |

## Release checklist

1. PR into `dev-v5` with the changelog in the **PR description** (it becomes the release
   notes). Merge → internal release `<base>-<epoch>` on call-team + tag on `dev-v5`.
   No version bump needed — every dev build gets a unique auto version.
2. For GA: bump `def libraryVersion` in `chat-sdk-android/build.gradle` (via a dev-v5 PR),
   then PR `dev-v5` → `master-v5` with the changelog in the description.
   Merge → public GA release of exactly that base version.
