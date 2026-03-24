@echo off
echo Building Release Candidate...
gradlew assembleReleaseCandidate --no-daemon --console=plain
if %ERRORLEVEL% EQU 0 (
    echo BUILD SUCCESSFUL
    echo Release Candidate APK location: app\build\outputs\apk\releaseCandidate\release\releaseCandidate.apk
) else (
    echo BUILD FAILED
)
pause
