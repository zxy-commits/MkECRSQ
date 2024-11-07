# MkECRSQ

Research on Multiple Enhanced k Combination Reverse Skyline Query Method

---

### Compilation

 + Mark the `data/` directory  as *Sources Root*.
 + Compile the project from `src/org/main/Experiments.java`.
 
### Running

 ```bash
 java -jar MkECRSQ.jar FILE_NAME GROUP_SIZE METHOD
 ```
 + `FILE_NAME` should be in accordance with `.txt` files under `data/`.
 + `COMBINATION_SIZE` should be a positive integer.
 + `METHOD` should be a value among 0, 1 and 2.
    * `0`: kCRSQ
    * `1`: ECRSQ
    * `2`: MkECRSQ


