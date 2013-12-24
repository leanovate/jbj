<?php
	// ---------------
	// INITIALIZE PAGE
	// ---------------
	require_once('scripts/sb_functions.php');
	global $logged_in;
	$logged_in = logged_in( true, false );

	$page_title = _sb('install02_title');
	require_once('scripts/sb_header.php');
	
	// ------------
	// PAGE CONTENT
	// ------------
	function create_folder( $dir ) {
		echo( 'Making <b>' . $dir . '</b> folder: ' );
		
		if ( !file_exists( $dir ) ) {
			// Creating Folder
			$oldumask = umask( 0 );
			$ok = mkdir( $dir, BLOG_MASK );
			umask( $oldumask );
			
			if ( !file_exists( $dir ) ) {
				// Failed
				echo( '<b style="color: red;">' . _sb('folder_failed') . '</b><br />' );
				return( -1 );
				
			} else {
				// Worked
				echo( '<b style="color: green;">' . _sb('folder_success') . '</b><br />' );
				return( 0 );
			}
			
		} else {
			// Folder Already Exists
				echo( '<b style="color: green;">' . _sb('folder_exists') . '</b><br />' );
			return( 0 );
		}
	}
	
	function page_content() {
		global $blog_config;
		
		// SUBJECT
		$entry_array = array();
		$entry_array[ 'subject' ] = _sb('install02_title');
		
		// PAGE CONTENT BEGIN
		ob_start();
		
		echo( _sb('install02_instructions') . '<p />' );
		
		echo( '<hr />' );
		
		$result = create_folder( CONFIG_DIR );
		$result = $result + create_folder( CONTENT_DIR );
		$result = $result + create_folder( IMAGES_DIR );

		// Create a .htaccess file as part of the install process...
		$htaccess_str = "IndexIgnore *

<Files .htaccess>
order allow,deny
deny from all
</Files>

<Files *.txt>
order allow,deny
deny from all
</Files>";

		sb_write_file( CONFIG_DIR.".htaccess", $htaccess_str );
		sb_write_file( CONTENT_DIR.".htaccess", $htaccess_str );
		//sb_write_file( IMAGES_DIR.".htaccess", $htaccess_str );
		
		echo( '<hr />' );
		echo( '<br />' );
		
		if ( $result < 0 ) {
			echo( _sb('help') . '<p />' );
			echo( '<a href="install02.php?blog_language=' . $blog_config->getTag('BLOG_LANGUAGE') . '">' . _sb('try_again') . '</a><p />' );
		} else {
			echo( _sb('install02_success') . '<p />' );
			echo( '<a href="install03.php?blog_language=' . $blog_config->getTag('BLOG_LANGUAGE') . '">' . _sb('continue') . '</a><p />' );
		}
		
		// PAGE CONTENT END
		$entry_array[ 'entry' ] = ob_get_clean();
		
		// THEME ENTRY
		echo( theme_staticentry( $entry_array ) );
	}

	require_once(ROOT_DIR . '/scripts/sb_footer.php');
?>
