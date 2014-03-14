<?php
    $a = array("Hello", "World", 42);

    for($i = 0; $i < count($a); $i++) {
        echo $a[$i];
        $a[$i] = 2 * $i;
        echo "\n";
    }

    for($i = 0; $i < count($a); $i++) {
        echo $a[$i];
        echo "\n";
    }
?>