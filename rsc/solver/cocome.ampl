option solver cplex.exe;
model cocome.mod;
data cocome.dat;
solve;
display {i in PRODUCT, j in STORE}: shipping_amount[i,j];
