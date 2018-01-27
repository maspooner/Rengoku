mkdir .\bin
cd bin
"C:\Program Files\Java\jdk1.8.0_131\bin\jar.exe" xfv ..\MRL.jar org
cd ..
"C:\Program Files\Java\jdk1.8.0_131\bin\javac.exe" -d ./bin -cp src;MRL.jar src/org/spooner/java/TileGame/*.java
if %errorlevel% NEQ 0 (goto done)
"C:\Program Files\Java\jdk1.8.0_131\bin\jar.exe" cvfm ./out/Rengoku.jar ./build-files/game-manifest.MF -C bin org assets
xcopy /i /y .\joy .\out\joy
xcopy /i /y .\build-files\run.bat .\out\run.bat
:done
PAUSE