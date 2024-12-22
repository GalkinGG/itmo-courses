log="2.log"

if [[ -f $log ]]
then
    rm $log
fi

for ((n = 1; n <= 20; n++))
do
    echo "$n:" >> $log
    for ((k = 0; k < 10; k++))
    do
        \time -f "%e" ./2b.sh $n 2>>$log
    done
done
