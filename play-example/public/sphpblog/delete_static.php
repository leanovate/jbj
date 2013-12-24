<?php
	// ---------------
	// INITIALIZE PAGE
	// ---------------
	require_once('scripts/sb_functions.php');
	global $logged_in;
	$logged_in = logged_in( true, true );
	$page_title = _sb('delete_static_title');

	// ---------------
	// POST PROCESSING
	// ---------------
	if ( array_key_exists( 'no', $_POST ) || array_key_exists( 'yes', $_POST ) ) {
		if ( array_key_exists( 'no', $_POST ) ) {
			// User clicked the "Cancel" button
			redirect_to_url( 'index.php' );
		} else {
			if ( array_key_exists( 'yes', $_POST ) ) {
				$entry = $_POST[ 'entry' ];
				$path = CONTENT_DIR.'static/';
				
				global $ok;
				if ( file_exists( $path . $entry . '.txt' ) ) {
					$ok = sb_delete_file( $path . $entry . '.txt' );
				}
				if ( file_exists( $path . $entry . '.txt.gz' ) ) {
					$ok = sb_delete_file( $path . $entry . '.txt.gz' );
				}
			
				if ( $ok === true ) {
					modify_link( 'delete_static', 'static.php?page='.$_POST[ 'entry' ] );
					
					redirect_to_url( 'index.php' );
				}
			}
		}
	}
	
	// ------------
	// PAGE CONTENT
	// ------------
	require_once('scripts/sb_header.php');

	function page_content() {
		global $blog_config, $ok;
		
		// SUBJECT
		$entry_array = array();
		$entry_array[ 'subject' ] = _sb('delete_static_title');
		
		// PAGE CONTENT BEGIN
		ob_start();
		
		if ( array_key_exists( "no", $_POST ) || array_key_exists( "yes", $_POST ) ) {
			// Check to see if we're posting data...
			global $ok;
			if ( $ok !== true ) {
				echo( _sb('delete_static_error') . $ok . '<p />' );
			} else {
				echo( _sb('delete_static_success') . '<p />' );
			}
			echo( '<a href="index.php">' . _sb('home') . '</a>' );
		} else {
			echo( _sb('delete_static_instructions') . '<p /><hr />');
			echo( get_static_entry_by_file( $_GET[ 'entry' ] ) );
			?>			
			<hr />
			<form action='delete_static.php' method="post">
				<input type="hidden" name="entry" value="<?php echo( $_GET[ 'entry' ] ); ?>">
				<input type="submit" name="yes" value="<?php echo( _sb('ok_btn') ); ?>" />
				<input type="submit" name="no" value="<?php echo( _sb('cancel_btn') ); ?>" />
			</form>
			<?php 
		}
		// PAGE CONTENT END
		$entry_array[ 'entry' ] = ob_get_clean();
		
		// THEME ENTRY
		echo( theme_staticentry( $entry_array ) ); 

		// THEME ENTRY
		$blog_content = read_entry_from_file( CONTENT_DIR.$_GET[ 'y' ].'/'.$_GET[ 'm' ].'/'.$_GET[ 'entry' ] );
		echo( $blog_content ); 

	}

	require_once(ROOT_DIR . '/scripts/sb_footer.php');

?>
