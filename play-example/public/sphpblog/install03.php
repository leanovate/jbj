<?php
	// ---------------
	// INITIALIZE PAGE
	// ---------------
	require_once('scripts/sb_functions.php');
	global $logged_in;
	$logged_in = logged_in( true, false );
	
	$page_title = _sb('install03_title');
	require_once('scripts/sb_header.php');

	
	// ------------
	// PAGE CONTENT
	// ------------
	function page_content() {
		global $blog_config;
		
		// SUBJECT
		$entry_array = array();
		$entry_array[ 'subject' ] = _sb('install03_title');
		
		// PAGE CONTENT BEGIN
		ob_start();
		
		echo( _sb('install03_instructions') . '<p />' );
		
		echo( _sb('supported'). '<br />');
		echo( _sb('standard') . ( CRYPT_STD_DES == 1 ? '<b style="color: green;">' . _sb('enabled') . '</b>' : '<b style="color: red;">' . _sb('disabled') . '</b>' ) . '<br />');
		echo( _sb('extended') . ( CRYPT_EXT_DES == 1 ? '<b style="color: green;">' . _sb('enabled') . '</b>' : '<b style="color: red;">' . _sb('disabled') . '</b>' ) . '<br />');
		echo( _sb('MD5') . ( CRYPT_MD5 == 1 ?'<b style="color: green;">' . _sb('enabled') . '</b>' : '<b style="color: red;">' . _sb('disabled') . '</b>' ) . '<br />');
		echo( _sb('blowfish') . ( CRYPT_BLOWFISH == 1 ? '<b style="color: green;">' . _sb('enabled') . '</b>' : '<b style="color: red;">' . _sb('disabled') . '</b>' ) . '<br />');
		echo('<br />');
	
		$hashtest = crypt('test');
		if ( strlen( $hashtest ) <= 13 ) {
			echo( _sb('using_standard') );
		} else if ( strlen( $hashtest ) <= 20 ) {
			echo( _sb('using_extended') );
		} else if ( substr( $hashtest, 0, 3 ) == '$1$' ) {
			echo( _sb('using_MD5') );
		} else if ( substr( $hashtest, 0, 3 ) == '$2$' || substr( $hashtest, 0, 4 ) == '$2a$' ) {
			echo( _sb('using_blowfish') );
		} else {
			echo( _sb('using_unknown') );
		}
		echo( str_replace( '%string', CRYPT_SALT_LENGTH, _sb('salt_length') ) );
		?>
		
		<hr />
		
		<form action="install04.php?blog_language=<?php echo( $blog_config->getTag('BLOG_LANGUAGE') ); ?>" method="post">
			<label for="user"><?php echo( _sb('username') ); ?></label><br />
			<input type="text" name="user" autocomplete="OFF" size="40"><br /><br />
			
			<label for="pass"><?php echo( _sb('password') ); ?></label><br />
			<input type="password" name="pass" autocomplete="OFF" size="40"><br /><br />
			
			<input type="submit" name="submit" value="<?php echo( _sb('submit_btn') ); ?>" />
		</form>
		
		<?php 
		// PAGE CONTENT END
		$entry_array[ 'entry' ] = ob_get_clean();
		
		// THEME ENTRY
		echo( theme_staticentry( $entry_array ) );
	}

	require_once(ROOT_DIR . '/scripts/sb_footer.php');	
?>
