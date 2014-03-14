<?php
    $a = array("Hello", "World", 42);

    for($i = 0; $i < count($a); $i++) {
        echo $a[$i];
        $a[$i] = ($i + 2) * $i + 1;
        echo "\n";
    }

    for($i = 0; $i < count($a); $i++) {
        echo $a[$i];
        echo "\n";
    }
?>