<?php
	// ---------------
	// INITIALIZE PAGE
	// ---------------
        //$page = "index";
	require_once('scripts/sb_functions.php');
	
	// Login
	global $logged_in;
	$logged_in = logged_in( false, true );
	
	// Create a session for the anti-spam cookie
	if ( !session_id() ) {
		session_start();
	}
	$_SESSION['cookies_enabled'] = '1';

	// ---------------
	// POST PROCESSING
	// ---------------
	
	// Verify information being passed in:
	//
	// index.php?d=12&m=11&y=05
	// index.php?entry=entry051128-213804
	// index.php?d=28&m=11&y=05&category=3
	// index.php?category=3
	//
	global $is_permalink;
	$is_permalink = true;
	
	$temp_year = null;
	if ( array_key_exists( 'y', $_GET ) ) {
		$is_permalink = false;
		if ( is_numeric( $_GET[ 'y' ]) && strlen( $_GET[ 'y' ] ) == 2 ) {
			$temp_year = $_GET[ 'y' ];
		}
	}
	$temp_month = null;
	if ( array_key_exists( 'm', $_GET ) ) {
		$is_permalink = false;
		if ( is_numeric( $_GET[ 'm' ]) && strlen( $_GET[ 'm' ] ) == 2 ) {
			$temp_month = $_GET[ 'm' ];
		}
	}
	$temp_day = null;
	if ( array_key_exists( 'd', $_GET ) ) {
		$is_permalink = false;
		if ( is_numeric( $_GET[ 'd' ]) && strlen( $_GET[ 'd' ] ) == 2 ) {
			$temp_day = $_GET[ 'd' ];
		}
	}
	$temp_entry = null;
	if ( array_key_exists( 'entry', $_GET ) ) {
		if ( strpbrk( $_GET[ 'entry' ], "/.\\%#;") === false) { // && strlen( $_GET[ 'entry' ] ) == 18 ) {
			$temp_entry = $_GET[ 'entry' ];
		}
	} else {
		$is_permalink = false;
		
		// This checks to index.php?entry061209-224649 or just ?entry061209-224649
		if (isset($_GET) && count($_GET)==1) {
			$keys = array_keys($_GET);
			$temp_entry = $keys[0];
			if ( strpbrk( $temp_entry, "/.\\%#;") === false) { // && strlen( $temp_entry ) == 18 ) {
				$is_permalink = true;
			} else {
				$temp_entry = null;
			}
		}
	}
	
	// Month / Year
	if ( !isset( $temp_year ) || !isset( $temp_month ) ) {
		// Set the $month, $year, $day globals...
		get_latest_entry();
	} else {
		// Grab $year and $month from URL
		global $month, $year;
		$year = $temp_year;
		$month = $temp_month;
	}
	
	// Day
	if ( isset( $temp_day ) ) {
		global $day;
		$day = $temp_day;
	}
	
	// Entry
	if ( isset( $temp_entry) ) {
		global $entry;
		$entry = $temp_entry;
	}
	
	// Category
	if ( array_key_exists( 'category', $_GET ) ) {
		global $category;
		$category = $_GET[ 'category' ];
		$is_permalink = false;
	}
	
	global $sb_info, $blog_config;
	
	// Check the option for specific category on first page...
	// If nothing was passed into this page, then use the default
	// category (cause it has to be the first page). WILL NEVER
	// OVERRIDE THE CATEGORY IF PASSED IN
	if ( array_key_exists( 'category', $_GET ) == FALSE ) {
		if ( $blog_config->getTag('BLOG_ENABLE_START_CATEGORY') == 1 AND empty($_GET['entry'])) { 
			$category = $blog_config->getTag('BLOG_ENABLE_START_CATEGORY_SELECTION');
		}
	}		 
	
	// ------------
	// PAGE CONTENT
	// ------------
	require_once('scripts/sb_header.php');
	
	function page_content() {
		global $month, $year, $day, $category, $logged_in, $entry, $is_permalink, $blog_config;

		$static = $blog_config->getTag('STATIC_HOME');
		if (!empty($static) && empty($_SERVER['QUERY_STRING'])) {
			// read static page here
			print theme_staticentry (read_static_entry ( $static, $logged_in ));
                        return;
		}
		
		$content = read_entries( $month, $year, $day, $logged_in, $entry, $category, $is_permalink );
		echo( $content);
	}
	
	require_once(ROOT_DIR . '/scripts/sb_footer.php');

?>
