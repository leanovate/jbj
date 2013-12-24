<?php
	// ---------------
	// INITIALIZE PAGE
	// ---------------
	require_once('scripts/sb_functions.php');
	global $logged_in;
	$logged_in = logged_in( true, true );

	$page_title = 'PHP Version: '.phpversion();
	require_once('scripts/sb_header.php');
	
	// ---------------
	// POST PROCESSING
	// ---------------
	
	// ------------
	// PAGE CONTENT
	// ------------
	function page_content() {
		global $blog_config, $ok, $sb_info;
	
		// SUBJECT
		$entry_array = array();
		$entry_array[ 'subject' ] = 'PHP Version: '.phpversion();
		
		// PAGE CONTENT BEGIN
		ob_start();
		printf("Simple PHP Blog Version %s (%s)", $sb_info[ 'version' ], $sb_info[ 'last_update' ]);
		phpini_check();
		print "<a href='phpinfoframe.php'>" . _sb("View PHP Info Full Screen") . "</a>";
		print "<iframe src='phpinfoframe.php' width='100%' height='600px' />";
		//phpinfo();
		
		// PAGE CONTENT END
		$entry_array[ 'entry' ] = ob_get_clean();
		
		// THEME ENTRY
		echo( theme_staticentry( $entry_array ) );
	}

	require_once(ROOT_DIR . '/scripts/sb_footer.php');
?>
