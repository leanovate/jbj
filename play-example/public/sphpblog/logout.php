<?php
	// ---------------
	// INITIALIZE PAGE
	// ---------------
	require_once('scripts/sb_functions.php');
	global $result;
	$result = logout();
	
	$logged_in = logged_in( false, true );

	$page_title = _sb('logout_title');
        // Extra Meta Data
        if ($result==1) {
                $head .= '<meta http-equiv="refresh" content="5; URL=index.php" />';
        }

	require_once('scripts/sb_header.php');
	
	// ---------------
	// POST PROCESSING
	// ---------------
	
	// ------------
	// PAGE CONTENT
	// ------------
	function page_content() {
		global $result;
	
		// SUBJECT
		$entry_array = array();
		$entry_array[ 'subject' ] = _sb('logout_title');
		
		// PAGE CONTENT BEGIN
		ob_start();
		
		switch ( $result ) {
			case 0:
				echo( _sb('logout_error') );
				break;
			case 1:
				echo( _sb('logout_success') );
				break;
			case -1:
				echo( _sb('error_no_cookie') );
				break;
		}
		
		echo( '<a href="index.php">' . _sb('home') . '</a>' );
		
		// PAGE CONTENT END
		$entry_array[ 'entry' ] = ob_get_clean();
		
		// THEME ENTRY
		echo( theme_staticentry( $entry_array ) );
		// 
	}

	require_once(ROOT_DIR . '/scripts/sb_footer.php');
?>
