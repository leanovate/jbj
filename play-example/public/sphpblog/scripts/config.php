<?php
	// Set all program variables here

	// Root Directory
	if (!defined('ROOT_DIR')){
		define('ROOT_DIR', dirname(dirname(__file__)) . '/');
	}
	
	// Base URL
	// You need to set this manually if you are using symlinks, sorry, this is a PHP limitation
	// we will assume this is a directory off of your root

function getbaseurl($root_dir, $docroot) {
	if ($_SERVER['SCRIPT_FILENAME'] == realpath($_SERVER['SCRIPT_FILENAME'])) {
                $delimiter = "/";
                if (strpos($docroot, "\\") !== FALSE) {
			$delimiter = "\\";
		}
                $pattern = str_replace($delimiter, "\/", $docroot);
		//print $pattern . "<br>";
                $root = str_replace("\\", "/", $root_dir);

		return preg_replace("/^" . $pattern . "/", "", $root);
	} else {
		return '/' . basename(dirname(dirname(__file__))) . '/';
	}
	return "";
}

        // SESSION LOCATION
        $sessionpath = session_save_path();
        if (strpos($sessionpath, ";") !== FALSE) {
                $sessionpath = substr($sessionpath, strpos ($sessionpath, ";")+1); // '5;/tmp'
        }
        define('SESSION_SAVE_PATH', $sessionpath); // Default is '/tmp'


/* for debugging
print getbaseurl(ROOT_DIR, $_SERVER["DOCUMENT_ROOT"]);
print "<br>";
print getbaseurl("D:\\Inetpub\\wwwroot\\blog\\", "D:\\Inetpub\\wwwroot");
print "<br>";
*/
	define('BASEURL', getbaseurl(ROOT_DIR, $_SERVER["DOCUMENT_ROOT"]));

	// Variables
	// define('USE_MOD_REWRITE', false);

	define('BLOG_MASK', 0775);
	
	// FOLDER LOCATIONS
	define('CONTENT_DIR',	ROOT_DIR.'content/');
	define('IMAGES_DIR',	ROOT_DIR.'images/');
	
	define('TEMPLATE_DIR',	ROOT_DIR.'templates/');
	
	define('CONFIG_DIR',	ROOT_DIR.'config/');
	define('CACHE_DIR',		CONFIG_DIR.'cache/');
	define('USER_DIR',		CONFIG_DIR.'users/');
	define('GROUP_DIR',		CONFIG_DIR.'groups/');
	
	define('CLASSES_DIR',	'classes/');
	
        // Last version and update information.
        //
        global $sb_info;
        $sb_info[ 'version' ] = "0.8.1";
        $sb_info[ 'last_update' ] = '07/23/12';

        //Remove timeout limit
        //@set_time_limit( 0 );

?>
