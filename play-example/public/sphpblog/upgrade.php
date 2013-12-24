<?php
	// ---------------
	// INITIALIZE PAGE
	// ---------------
	require_once('scripts/sb_functions.php');
	global $logged_in;
	$logged_in = logged_in( true, true );

	$page_title = _sb('Upgrade');
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
		$entry_array[ 'subject' ] = _sb('Upgrade');
		
		// PAGE CONTENT BEGIN
		ob_start();
		
		echo ( 'Moved ' . move_all_comment_files( true ) . ' comment files...');
		
		// PAGE CONTENT END
		$entry_array[ 'entry' ] = ob_get_clean();

		// THEME ENTRY
		echo( theme_staticentry( $entry_array ) );
	}

	require_once(ROOT_DIR . '/scripts/sb_footer.php');	
?>
