#!/bin/bash

TRASHDIR=$HOME/.trash/
TRASHLOG=$HOME/.trash.log


if [[ $# -ne 1 ]]
then
    echo "Error! Expected 1 argument, found $#"
    exit 1
fi

if [[ ! -f $1 ]]
then
    echo "Error! File '$1' not found"
    exit 1
fi

if [[ ! -d $TRASHDIR ]] 
then
  mkdir $TRASHDIR
fi

if [[ ! -f $TRASHLOG ]] 
then
  touch $TRASHLOG
fi

num=1
currName=$1$num

while [[ -f $TRASHDIR$currName ]]
do
    let num=$num+1
    currName=$1$num
done

ln $(realpath $1) $TRASHDIR$currName
rm -f $1

echo $(realpath $1) $currName >> $TRASHLOG
