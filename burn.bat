"C:\Program Files\Java\jdk1.8.0_131\bin\jar.exe" cvfm ./out/Installer.jar ./build-files/installer-manifest.MF -C bin org/spooner/java/TileGame/Utils wizard.png wizIcon.png
xcopy /i /y .\build-files\installer.bat .\out\installer.bat
PAUSE