<?php

function specialSum($a, $b) {
    if ($a < 10) {
        return 2 * $a + $b;
    } else {
        return $a + $b;
    }
}

echo specialSum(2, 3);
echo "\n";
echo specialSum(6, 7);
echo "\n";
echo specialSum(12, 13);
echo "\n";
?>