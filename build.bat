mkdir .\bin
mkdir .\out
cd bin
jar.exe xfv ..\MRL.jar org
cd ..
javac.exe -d ./bin -cp src;MRL.jar src/org/spooner/java/TileGame/*.java 
if %errorlevel% NEQ 0 (goto done)
jar.exe cvfm ./out/Rengoku.jar ./build-files/game-manifest.MF -C bin org assets
echo d | xcopy /i /y .\joy .\out\joy
echo f | xcopy /i /y .\build-files\run.bat .\out\run.bat
:done
PAUSE
