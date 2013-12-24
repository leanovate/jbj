<?php
	// ---------------
	// INITIALIZE PAGE
	// ---------------
	require_once('scripts/sb_functions.php');
	global $logged_in;
	$logged_in = logged_in( true, false );

	$page_title = _sb('install01_title');
	require_once('scripts/sb_header.php');
	
	// ------------
	// PAGE CONTENT
	// ------------
	function page_content() {
		global $blog_config;
		
		// SUBJECT
		$entry_array = array();
		$entry_array[ 'subject' ] = _sb('install01_title');
		
		// PAGE CONTENT BEGIN
		ob_start();
		
		echo( _sb('install01_instructions') . '<p />' );

		echo( '<a href="install02.php?blog_language=' . $blog_config->getTag('BLOG_LANGUAGE') . '">' . _sb('begin') . '</a><p />' );

                print "<p>" . printf(_sb("We think your blog is at %s."), $_SERVER["SERVER_NAME"] . BASEURL) . " ";
		print _sb("If not, set BASEURL correctly in scripts/config.php") . "</p>";

		phpini_check();
		
		// PAGE CONTENT END
		$entry_array[ 'entry' ] = ob_get_clean();
		
		// THEME ENTRY
		echo( theme_staticentry( $entry_array ) );
	}

	require_once(ROOT_DIR . '/scripts/sb_footer.php');
?>
