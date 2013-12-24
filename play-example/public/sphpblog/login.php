<?php
	// ---------------
	// INITIALIZE PAGE
	// ---------------
	$page = 'login';
	require_once('scripts/sb_functions.php');
	global $logged_in;
	$logged_in = logged_in( false, true );

$page_title = _sb('login_title');

	require_once('scripts/sb_header.php');
	
	// ---------------
	// POST PROCESSING
	// ---------------

		if ($_SERVER["HTTPS"] != 'on') {
		    if ($blog_config->getTag('HTTPS') == 'REQUIRE') {
                        $url = $blog_config->getTag('HTTPS_URL');
                        if (empty($url)) {
			    $url = str_replace('http://', 'https://', sb_curPageURL()); 
			}
			header("Location: $url");
		    }
		}
	
	// ------------
	// PAGE CONTENT
	// ------------
	function page_content() {
		global $logged_in, $theme_vars, $blog_theme, $blog_config;
		
		// SUBJECT
		$entry_array = array();
		$entry_array[ 'subject' ] = _sb('login_title');
			
		// PAGE CONTENT BEGIN
		ob_start(); 

		if ($_SERVER["HTTPS"] != 'on') {
		    if ($blog_config->getTag('HTTPS') == 'WARN') {
			print "<p style='background-color: red; color: white'>" . _sb("WARNING: Password will be sent unencrypted!") . "</p>"; 
                        $url = $blog_config->getTag('HTTPS_URL');
                        if (empty($url)) {
			    $url = str_replace('http://', 'https://', sb_curPageURL()); 
			}
			print "<p><a href='$url'>Attempt secure mode</a>.</p>"; 
		    }
		}

?>
		<p><?php echo( _sb('login_instructions') ); ?></p>
		
		<hr />
		
		<form action="login_cgi.php" method="post">
			<label for="user"><?php echo( _sb('username') ); ?></label><br />
			<input type="text" name="user" size="40"><p />
			
			<label for="pass"><?php echo( _sb('password') ); ?></label><br />
			<input type="password" name="pass" size="40"><p />
			
			<input type="submit" name="submit" value="<?php echo( _sb('submit_btn') ); ?>" />
		</form>
		
		<?php 

                $restored = restore_post();

                if (!empty($restored)) {
                        $type = $restored[0];
                        echo( "<p>" . _sb("Do not worry!  Your draft has been saved, login to restore your last draft.") . "</p>");
                }


		// PAGE CONTENT END
		$entry_array[ 'entry' ] = ob_get_clean();
		
		// THEME ENTRY
		echo( theme_staticentry( $entry_array ) );
	}
	
require_once(ROOT_DIR . '/scripts/sb_footer.php');

?>
