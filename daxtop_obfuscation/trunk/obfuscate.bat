@ECHO OFF
call clean.bat
mkdir .\output\Networks
mkdir .\release\
mkdir .\moderator_release\
echo Obfuscation started...
@ECHO ON
java -jar ./proguard/lib/proguard.jar @config.pro
@ECHO OFF
echo Generating release files...
xcopy /E /Y "..\dp-projects\Daxtop\dist\*.*" ".\release\"
xcopy /Y "..\dp-projects\Daxtop\splash.png" ".\release\"
xcopy /E /Y "..\dp-projects\Daxtop\networks\*.*" ".\release\networks\"
xcopy /E /Y ".\output\*.*" ".\release\"
del /S /Q ".\release\README.txt"
xcopy /E /Y "..\dp-projects\Daxtop\dist\*.*" ".\moderator_release\"
xcopy /Y "..\dp-projects\Daxtop\splash.png" ".\moderator_release\"
xcopy /E /Y "..\dp-projects\Daxtop\networks\*.*" ".\moderator_release\networks\"
xcopy /E /Y ".\output\*.*" ".\moderator_release\"
xcopy /E /Y "..\dp-projects\MXitCore\res\chat_zone_commands\moderators" ".\moderator_release\networks\"
del /S /Q ".\moderator_release\README.txt"
echo Generating executables for standard version...
.\launch4j\launch4jc.exe daxtop_exe.xml
del /S /Q ".\release\Daxtop.jar"
echo Generating executables for moderator version...
.\launch4j\launch4jc.exe daxtop_exe_mod.xml
del /S /Q ".\moderator_release\Daxtop.jar"
pause