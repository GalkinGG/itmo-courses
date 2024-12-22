#!/bin/bash

while true
do
    read LINE
    case $LINE in
        "+") 
            kill -USR1 $(cat .pidProc)
            ;;
        "*")
            kill -USR2 $(cat .pidProc)
            ;;
        TERM)
            kill -SIGTERM $(cat .pidProc)
            exit
            ;;
        *)
            :
            ;;
    esac
done
