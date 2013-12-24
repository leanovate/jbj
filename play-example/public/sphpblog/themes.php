<?php
	// ---------------
	// INITIALIZE PAGE
	// ---------------
	require_once('scripts/sb_functions.php');
	global $logged_in;
	$logged_in = logged_in( true, true );

	$page_title = _sb('themes_title');
	
	// ---------------
	// POST PROCESSING
	// ---------------
	if ( array_key_exists( 'blog_theme', $_POST ) ) {
		$new_config = new Configuration();
		$new_config->read_file();
		
		// @htmlspecialchars( $str, ENT_QUOTES, $GLOBALS['lang_string']['php_charset'] );
		$new_config->setTag('BLOG_THEME', 	sb_stripslashes($_POST['blog_theme']));
		
		global $ok;
		$ok = $new_config->write_file();
								
		if ( $ok === true ) { 
			redirect_to_url( 'index.php' );
		}
	}
	
	// ------------
	// PAGE CONTENT
	// ------------
	require_once('scripts/sb_header.php');
	function page_content() {
		global $user_colors, $logged_in, $theme_vars, $blog_theme, $ok;
		
		// SUBJECT
		$entry_array = array();
		$entry_array[ 'subject' ] = _sb('themes_title');
		
		// PAGE CONTENT BEGIN
		ob_start();
		
		if ( array_key_exists( 'blog_theme', $_POST ) ) {
			// Check to see if we're posting data...
			global $ok;
			if ( $ok !== true ) {
				echo( _sb('upload_img_error') . $ok . '<p />' );
			} else {
				echo( _sb('upload_img_success') . '<p />' );
			}
			
			echo( '<a href="index.php">' . _sb('home') . '</a>' );
			
		} else {
			?>
			
			<?php echo( _sb('themes_instructions') ); ?><p />
			
			<hr noshade size="1" color="#<?php echo(get_user_color('inner_border_color')); ?>" />
			
			<form action="themes.php" method="post" name="setup" name="setup">
				
				<label for="blog_theme"><?php echo( _sb('choose_theme') ); ?></label><br />
				<select name="blog_theme">
				<?php
						$dir = 'themes/';
						
						clearstatcache();
						if ( is_dir($dir) ) {
							$dhandle = opendir($dir);
							if ( $dhandle ) {
								$sub_dir = readdir( $dhandle );
								while ( $sub_dir ) {
									if ( is_dir( $dir . $sub_dir ) == true && $sub_dir != '.' && $sub_dir != '..' ) {
										$lang_dir = $sub_dir;
										$lang_name = sb_read_file( $dir . $sub_dir . '/id.txt' );
										if ( $lang_name ) {
											$str = '<option label="' . $lang_name . '" value="' . $lang_dir . '"';
											if ( $blog_theme == $lang_dir ) {
												$str	.= ' selected';
											}
											$str	.= '>' . $lang_name . '</option>';
											
											echo( $str );
										}
									}
									$sub_dir = readdir( $dhandle );
								}
							}
							closedir( $dhandle );
						}
					?>
				
				</select><br />
				
				<hr noshade size="1" color="#<?php echo(get_user_color('inner_border_color')); ?>" />
				
				<input type="submit" name="submit" value="<?php echo( _sb('submit_btn') ); ?>" /><br /><br />
			</form>
			
			<?php 
		}
		// PAGE CONTENT END
		$entry_array[ 'entry' ] = ob_get_clean();
		
		// THEME ENTRY
		echo( theme_staticentry( $entry_array ) );
	}

	require_once(ROOT_DIR . '/scripts/sb_footer.php');
?>
