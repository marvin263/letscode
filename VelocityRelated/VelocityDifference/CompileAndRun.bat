@echo off
echo compile and run 1.7...
javac -cp 1.7/lib/velocity-1.7-dep.jar; -d 1.7/classes/ *.java
java  -cp 1.7/lib/velocity-1.7-dep.jar;1.7/classes Example 1.7

echo compile and run 1.3.1...
javac -cp 1.3.1/lib/velocity-dep-1.3.1.jar; -d 1.3.1/classes/ *.java
java  -cp 1.3.1/lib/velocity-dep-1.3.1.jar;1.3.1/classes Example 1.3.1
pause;