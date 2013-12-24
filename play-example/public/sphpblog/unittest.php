<?php


$files = scandir(".");
foreach ($files as $filename) {
    if (!is_dir($filename) AND basename(__FILE__) != $filename) {
        print "Checking $filename ...\n";
        exec("php $filename", $output, $result);
        if ($result) {
            print "ERROR in $filename ($result).\n";
        }
    }
}


?>
