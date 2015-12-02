#!/bin/bash
for i in `seq 3 96`;
do
	cp GBM_C1.java GBM_C$i.java
	sed -i "s/GBM_C1/GBM_C$i/g" GBM_C$i.java
done    

