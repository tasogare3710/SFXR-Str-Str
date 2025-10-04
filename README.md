# SFXR-Str-Str

## Build

```
javac -d src --module-source-path tasogare.sfxr=src --module tasogare.sfxr
jar -c -f out/tasogare.sfxr.jar -C src/tasogare.sfxr ./
```

## Run

```
cd out
java -Djava.util.logging.config.file=../debug.logging.properties -Dswing.defaultlaf=javax.swing.plaf.nimbus.NimbusLookAndFeel -p . -m tasogare.sfxr/com.github.tasogare.sfxr.app.Application
```

or

```
cd out
java -Djava.util.logging.config.file=../debug.logging.properties -Dawt.useSystemAAFontSettings=on -Dsun.java2d.xrender=True -Dswing.defaultlaf=javax.swing.plaf.nimbus.NimbusLookAndFeel -p . -m tasogare.sfxr/com.github.tasogare.sfxr.app.Application
```
