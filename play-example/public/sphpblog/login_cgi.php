<?php
	// ---------------
	// INITIALIZE PAGE
	// ---------------
	require_once('scripts/sb_functions.php');
	global $logged_in;
	$logged_in = logged_in( false, true );

	// ---------------
	// POST PROCESSING
	// ---------------
	$ok = check_password( sb_stripslashes( $_POST[ 'user' ] ), sb_stripslashes( $_POST[ 'pass' ] ) );

	if ( $ok > 99 ) {
		$logged_in = false;
	} else {
		$logged_in = $ok;
	}

	$page_title = _sb('login_title');
	require_once('scripts/sb_header.php');

	// ------------
	// PAGE CONTENT
	// ------------
	function page_content() {
		global $blog_config, $ok;
		
		// SUBJECT
		$entry_array = array();
		$entry_array[ 'subject' ] = _sb('login_title');
		
		// PAGE CONTENT BEGIN
		ob_start();
		
		if ( $ok === true ) {
			echo( _sb('login_success') );
		} else {
			switch ($ok) {
				case 100: $errortext = _sb('login_wrong_password');
				case 101: $errortext = _sb('inactive_account');
			}
			echo( $errortext );
		}

		$restored = restore_post();

		if (empty($restored)) {	
			echo( '<a href="index.php">' . _sb('home') . '</a>' );
		} else {
			$type = $restored[0];
			$page = 'add.php';
			if ($type == 'static') {
				$page = 'add_static.php';
			}
			echo( "<a href=\"$page\">" . _sb("Restore your last draft.") . "</a>" );
		}
		
		$upgrade_count = move_all_comment_files( true, true );
		if ( $upgrade_count > 0 ) {
			echo( "<hr />" );
			echo( "<br />" );
			echo( _sb('upgrade') );
			$str = str_replace ( '%n', $upgrade_count, _sb('upgrade_count') );
			echo( $str . "<br /><br />" );
			echo( "<a href=\"upgrade.php\">" . _sb('upgrade_url') ."</a><br />" );
		}
		
		// PAGE CONTENT END
		$entry_array[ 'entry' ] = ob_get_clean();
		
		// THEME ENTRY
		echo( theme_staticentry( $entry_array ) );
	}

	require_once(ROOT_DIR . '/scripts/sb_footer.php');
?>
