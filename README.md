# Dexter
A work-in-progress **DEX** editor (hence the name **Dex**ter) for Android that uses [smali](https://github.com/JesusFreke/smali).

## Available decompilers
- [JADX](https://github.com/skylot/jadx)
- [Fernflower](https://github.com/JetBrains/intellij-community/tree/master/plugins/java-decompiler/engine)
- [CFR](https://github.com/leibnitz27/cfr)
- [JD-Core](https://github.com/java-decompiler/jd-core)
- [Procyon](https://github.com/mstrobel/procyon)

## TO-DO
- Implement recompilation of decompiled Java sources.
- Decompile inner/anonymous classes as well.
- Update Jadx to 1.2.0.
- Add smali navigation, decompiling single method bodies.
- Underline syntax errors in smali in realtime.
- Add "Goto" for fields/methods.
- Add a (dis)assembler for Java, like [Krakatau](https://github.com/Storyyeller/Krakatau).

## Non-Goals
- Making it a full-fledged APK editor (Things such as ARSC editing are **OFF** the table.)

## License
Dexter is licensed under **GNU General Public License v3.0**, see [LICENSE](https://github.com/MikeAndrson/Dexter/blob/master/LICENSE) for more.
