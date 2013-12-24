<?php

flush();

if (function_exists('page_content')) {
        if (function_exists('theme_pagelayout')) {
                theme_pagelayout();
        } else {
                page_content();
        }
}

?>

</html>
