INT num1, num2, result, counter;

num1 := 5;
num2 := 7;
counter := 1;
result := 0;

WHILE counter <= num2
DO
    IF counter * 2 > num1
    THEN
        result := result + (num1 - counter);
    ELSE
        result := result - counter;
    FI;
    
    counter := counter + 1;
OD;

PRINT result;