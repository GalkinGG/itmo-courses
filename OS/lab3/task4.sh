#!/bin/bash

./task4_1.sh &
./task4_2.sh &
./task4_3.sh &

cpulimit -b -p $(cat .pid1) -l 10
kill $(cat .pid3)
