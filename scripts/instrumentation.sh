echo 'Running CometChat Test Cases'
touch ~/logs.txt
~/Library/Android/sdk/platform-tools/./adb shell am instrument -w -r -e debug false -e class 'com.cometchat.chat.suite.CometChatTestSuite' com.cometchat.chat.test/androidx.test.runner.AndroidJUnitRunner | tee ~/logs.txt

searchString='INSTRUMENTATION_STATUS_CODE: -2'
file=~/logs.txt

if [ -f "$file" ]; then
    echo "$file exists."
fi

## Checking for the failure/success
echo 'Checking success/failure'
resultsFile=~/results.txt
if grep -q "$searchString" $file;then
echo "-1">$resultsFile
else
echo "0">$resultsFile
fi

# Deleting the temp log file
echo 'Deleting the temporary log file'
rm ~/logs.txt


