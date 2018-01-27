javac.exe -d ./bin -cp src src/org/spooner/java/TileGame/Utils/Installer.java
if %errorlevel% NEQ 0 (goto done)
jar.exe cvfm ./out/Installer.jar ./build-files/installer-manifest.MF -C bin org/spooner/java/TileGame/Utils wizard.png wizIcon.png
echo f | xcopy /i /y .\build-files\installer.bat .\out\installer.bat
:done
PAUSE
