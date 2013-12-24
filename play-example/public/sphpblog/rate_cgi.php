<?php
	// ---------------
	// INITIALIZE PAGE
	// ---------------
	require_once('scripts/sb_functions.php');
	global $logged_in;
	$logged_in = logged_in( false, true );
	$page_title = _sb('rating_title');
	
	// ---------------
	// POST PROCESSING
	// ---------------
	if ( array_key_exists( 'y', $_GET ) && array_key_exists( 'm', $_GET ) && array_key_exists( 'entry', $_GET ) && array_key_exists( 'rating', $_GET ) ) {
		$rating = intval( $_GET[ 'rating' ] );
		if ( $rating >= 1 && $rating <= 5 ) {		
			if ( strpos( $_GET[ 'y' ], array( '/', '.', '\\', '%' ) ) === false && strlen( $_GET[ 'y' ] ) == 2 &&
					strpos( $_GET[ 'm' ], array( '/', '.', '\\', '%' ) ) === false && strlen( $_GET[ 'm' ] ) == 2 &&
					strpos( $_GET[ 'entry' ], array( '/', '.', '\\', '%' ) ) === false && strlen( $_GET[ 'entry' ] ) == 18 ) {
				$y = $_GET[ 'y' ];
				$m = $_GET[ 'm' ];
				$entry = $_GET[ 'entry' ];
				if ( entry_exists( $y, $m, $entry ) ) {
					write_rating( $y, $m, $entry, $rating );
				}
			}
		}
	}
	
	// Even if it failed to store the vote, we still want to return to the home page.
	$ok = true;
	if ( $ok === true ) {
		redirect_to_url( 'index.php' );
	}
	
	// ------------
	// PAGE CONTENT
	// ------------
	require_once('scripts/sb_header.php');

	function page_content() {
		global $blog_config, $ok;
		
		// SUBJECT
		$entry_array = array();
		$entry_array[ 'subject' ] = _sb('rating_title');
		
		// PAGE CONTENT BEGIN
		ob_start();
		
		if ( $ok !== true ) {
			echo( _sb('rating_error') . $ok . '<p />' );
		} else {
			echo( _sb('rating_success') . '<p />' );
		}
		echo( '<a href="index.php">' . _sb('home') . '</a>' );
		
		// PAGE CONTENT END
		$entry_array[ 'entry' ] = ob_get_clean();
		
		// THEME ENTRY
		echo( theme_staticentry( $entry_array ) );
	}
	require_once(ROOT_DIR . '/scripts/sb_footer.php');

?>
