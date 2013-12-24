<?php
	// ---------------
	// INITIALIZE PAGE
	// ---------------
	require_once('scripts/sb_functions.php');
	global $logged_in;
	$logged_in = logged_in( true, true );

	$page_title = _sb('set_login_title');
	require_once('scripts/sb_header.php');
	
	// ---------------
	// POST PROCESSING
	// ---------------
	
	// ------------
	// PAGE CONTENT
	// ------------
	function page_content() {
	global $blog_config;
	
	// SUBJECT
	$entry_array = array();
	$entry_array[ 'subject' ] = _sb('set_login_title');
	
	// PAGE CONTENT
	$entry_array[ 'entry' ] = _sb('explanation');
	
	// THEME ENTRY
	echo( theme_staticentry( $entry_array ) );		
	}

	require_once(ROOT_DIR . '/scripts/sb_footer.php');
	
?>
