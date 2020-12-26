# How to use

1. copy and paste `test.in` to `(your_path)/HW5/problem1/mytest`.
2. add `configure_file(mytest/test.in mytest/test.in COPYONLY)` to `CMakeLists.txt`.
3. set `bool print_cout` to `true` in `int main()`.
4. run the following commands in terminal. (not sure if it works in Windows too...)

```bash
$ cd (your_path)/HW5/problem1
$ (your_path)/HW5/cmake-build-debug/problem1/problem1 > mytest/mytest.out
```

5. compare `mytest.out` with `test.out`.
6. make sure to undo step 2-3 before submitting.
