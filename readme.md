This repository contains a simple implementation of AES-128. The implementation is verified using a cryptol spesification made by Galois. 

To run the verification compile all java files with debug information ```javac -g``` and then run 
``` bash
saw AES.saw
```
