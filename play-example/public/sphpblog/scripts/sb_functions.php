<?php
	// Do all common standard imports and setup here

	// Simple PHP Blog is released under the GNU Public License.
	// Error reporting should be set to 0 in production environments.
	error_reporting( E_ALL ^ E_NOTICE );
	// use this for production
	error_reporting( 0 );

	require_once("config.php");	
        /*require_once(CLASSES_DIR.'fileio.php');
        require_once(CLASSES_DIR.'arrays.php');
        require_once(CLASSES_DIR.'template.php');
        require_once(CLASSES_DIR.'html.php');
        require_once(CLASSES_DIR.'container.php');
        require_once(CLASSES_DIR.'configuration.php');
        require_once(CLASSES_DIR.'user.php');
        require_once(CLASSES_DIR.'group.php');*/

       // Read configuration file
        require_once('sb_config.php');
        read_config();

        require_once('sb_forms.php');
        require_once(ROOT_DIR . "/languages/sb_lang.php");

        // Load language strings

        // Validate Language
        $lang = 'english';
        if ( isset( $_REQUEST['blog_language'] ) ) {
                $temp_lang = '';
                $temp_lang = sb_stripslashes( $_REQUEST['blog_language'] );
                if (validate_language($temp_lang) !== false) {
                    $lang = $temp_lang;
                }
                //$blog_config->setTag('BLOG_LANGUAGE', $temp_lang);
        } else {
               $lang = $GLOBALS['blog_config']->getTag('BLOG_LANGUAGE');
        }

        sb_import_lang($lang);

        require_once('sb_login.php');
        require_once('sb_theme.php');
        require_once('sb_formatting.php');
        require_once('sb_emoticons.php');
        require_once('sb_date.php');
        require_once('sb_communicate.php');
        require_once('sb_comments.php');
        require_once('sb_static.php');
        require_once('sb_utility.php');
        require_once('sb_menu.php');
        require_once('sb_search.php');
        require_once('sb_entry.php');
        require_once('sb_image.php');
        require_once('sb_display.php');
        //require_once('sb_color.php'); // These functions don't get used
        require_once('sb_feed.php');
        require_once('sb_categories.php');
        require_once('sb_texteditor.php');
        require_once('sb_counter.php');
        require_once('sb_blacklist.php');

        require_once('sb_sidebar.php');

        // Store "time" for benchmarking.
        function getmicrotime() {
                return( microtime( true ) );
        }

        global $page_timestamp;
        $page_timestamp = getmicrotime();

?>
