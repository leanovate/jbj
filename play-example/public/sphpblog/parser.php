<?php

        // ---------------
        // INITIALIZE PAGE
        // ---------------
        require_once('scripts/sb_functions.php');
        global $logged_in;
        $logged_in = logged_in( false, false );

	if (!$logged_in) {
            exit;
        }

        require_once('scripts/sb_header.php');

        $text = blog_to_html($_REQUEST['data'], false, false, false, true );
        $text = '<body><div id="maincontent">' . $text . '</div></body>';
        echo($text);

	require_once(ROOT_DIR . '/scripts/sb_footer.php');
?>
