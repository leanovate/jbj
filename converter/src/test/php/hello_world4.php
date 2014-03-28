<?php

function specialSum($a, $b) {
    if ($a < 10) {
        return 2 * $a + $b;
    } else {
        return $a + $b;
    }
}

function specialLoop($a, $count) {
    $i = 1;
    while($i < $count) {
        $a += $i;
        ++$i;
    }
    return $a;
}
echo specialSum(2, 3);
echo "\n";
echo specialSum(6, 7);
echo "\n";
echo specialSum(12, 13);
echo "\n";
echo specialLoop(2, 5);
echo "\n";
echo specialLoop(12, 13);
echo "\n";
?>