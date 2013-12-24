<?php
	// ---------------
	// INITIALIZE PAGE
	// ---------------
	require_once('scripts/sb_functions.php');
	$page_title =_sb('comments_title');

	function strposa($haystack ,$needles=array(),$offset=0){
		$chr = array();
		foreach($needles as $needle){
			if (strpos($haystack,$needle,$offset) !== false) {
				$chr[] = strpos($haystack,$needle,$offset);
			}
		}
		if(empty($chr)) return false;
		return min($chr);
	}

	// Login
	global $logged_in;
	$logged_in = logged_in( false, true );

	// Create a session for the anti-spam cookie
	if ( !session_id() ) {
		session_start();
	}

	// Extra Javascript
	ob_start();
	print sb_editor_js('commment_text');
	$head .= ob_get_clean();

	// ---------------
	// POST PROCESSING
	// ---------------

	// Verify information being passed:
	$redirect = true;
	if ( array_key_exists( 'y', $_GET ) && array_key_exists( 'm', $_GET ) && array_key_exists( 'entry', $_GET ) ) {
		// Dis-allow dots, and slashes to make sure the
		// user is not able to back-up a directory.
		//
		// Make sure the string lengths are correct.
		if ( strposa( $_GET[ 'y' ], array( '/', '.', '\\', '%' ) ) === false && strlen( $_GET[ 'y' ] ) == 2 &&
				strposa( $_GET[ 'm' ], array( '/', '.', '\\', '%' ) ) === false && strlen( $_GET[ 'm' ] ) == 2 &&
				strposa( $_GET[ 'entry' ], array( '/', '.', '\\', '%' ) ) === false && strlen( $_GET[ 'entry' ] ) == 18 ) {

			// Verify that the file exists.
			if ( entry_exists ( $_GET[ 'y' ], $_GET[ 'm' ], $_GET[ 'entry' ] ) ) {
				$_SESSION[ 'capcha_' . $_GET[ 'entry' ] ] = sb_get_capcha();

				$GLOBALS[ 'year' ] = substr($_GET[ 'entry' ], 5, 2);
				$GLOBALS[ 'month' ] = substr($_GET[ 'entry' ], 7, 2);
				$GLOBALS[ 'day' ] = substr($_GET[ 'entry' ], 9, 2);

				$redirect = false;
			}
		}
	}

	if ( $redirect === true ) {
		redirect_to_url( 'index.php' );
	}
	
	// ------------
	// PAGE CONTENT
	// ------------
	require_once('scripts/sb_header.php');
	function page_content() {
		global $user_colors, $logged_in, $theme_vars, $blog_theme, $blog_config;

		// show entry
		$filename = CONTENT_DIR.$_GET[ 'y' ].'/'.$_GET[ 'm' ].'/'.$_GET[ 'entry' ];
		$blog_content = read_entry_from_file( $filename );
		$blog_content = replace_more_tag ( $blog_content , true, '' );
		print $blog_content;

		// show comments
		echo "<h2>" . _sb('comments_title') . "</h2>";
		echo( read_comments( $_GET[ 'y' ], $_GET[ 'm' ], $_GET[ 'entry' ], $logged_in ) );
		echo( '<p />' );

		// SUBJECT
		$entry_array = array();
		$entry_array[ 'subject' ] = _sb('comments_header');

		// ADD COMMENT FORM
		ob_start(); ?>

		<?php echo( _sb('comments_instructions') ); ?><p />

		<form action='comment_add_cgi.php' method="post" name="vbform">
			<input type="hidden" name="y" value="<?php echo( $_GET[ 'y' ] ); ?>" />
			<input type="hidden" name="m" value="<?php echo( $_GET[ 'm' ] ); ?>" />
			<input type="hidden" name="entry" value="<?php echo( $_GET[ 'entry' ] ); ?>" />

			<?php
			if ($GLOBALS['logged_in']==false ) {
				echo('<label for="comment_name">' . _sb('comment_name') . '</label><br />');
				echo('<input type="text" name="comment_name" id="comment_name" value="' . $_COOKIE[ 'comment_name' ] . '" autocomplete="off" /><br />');
			} else {
				$admin = $_SESSION[ 'fulladmin' ];
				if ($admin == 'yes' ) {
					echo('<input type="hidden" name="comment_name" id="comment_name" value="' . $_SESSION[ 'user' ] . ' (' . $blog_config->getTag('BLOG_AUTHOR') . ')" autocomplete="off" />');
				} else {
					echo('<input type="hidden" name="comment_name" id="comment_name" value="' . $_SESSION[ 'user' ] . '" autocomplete="off" />'); }
			}

			if ($GLOBALS['logged_in']==false ) {
				echo('<label for="comment_email">' . _sb('comment_email') . '</label><br />');
				echo('<input type="text" name="comment_email" id="comment_email" value="' . $_COOKIE[ 'comment_email' ] . '" autocomplete="off" /><br />');
			} else {
				// Blank Email
				echo('<input type="hidden" name="comment_email" id="comment_email" value="" autocomplete="off" />');
			}

			if ($GLOBALS['logged_in']==false ) {
				echo('<label for="comment_url">' . _sb('comment_url') . '</label><br />');
				echo('<input type="text" name="comment_url" id="comment_url" value="' . $_COOKIE[ 'comment_url' ] . '" autocomplete="off" /><br />');
				echo('<label for="comment_remember">' . _sb('comment_remember') . '<input type="checkbox" name="comment_remember" id="comment_remember" value="1"');
				echo(' autocomplete="off" /></label><br /><br />');
			} else {
				// Blank URL
				echo('<input type="hidden" name="comment_url" id="comment_url" value="" autocomplete="off" />');
			}
			?>

			<!-- NEW -->
			<?php echo( _sb('label_insert') ); ?><br />
			<?php
			global $blog_config;
			$jsstr = "";

			$allowed = explode(',', $blog_config->getTag('COMMENT_TAGS_ALLOWED'));

			if ( in_array( 'b', $allowed ) ) {
				$jsstr .= '<input type="button" class="bginput" value="' . _sb('btn_bold') . '" onclick="ins_styles(this.form.comment_text,\'b\',\'\');" />';
			}
			if ( in_array( 'i', $allowed ) ) {
				$jsstr .= '<input type="button" class="bginput" value="' . _sb('btn_italic') . '" onclick="ins_styles(this.form.comment_text,\'i\',\'\');" />';
			}
			if ( in_array( 'center', $allowed ) ) {
				$jsstr .= '<input type="button" class="bginput" value="center" onclick="ins_styles(this.form.comment_text,\'center\',\'\');" />';
			}
			if ( in_array( 'url', $allowed ) ) {
				$jsstr .= '<input type="button" class="bginput" value="' . _sb('btn_url') . '" onclick="ins_url_no_options(this.form.comment_text);" />';
			}
			if ( in_array( 'img', $allowed ) ) {
				$jsstr .= '<input type="button" class="bginput" value="' . _sb('btn_image') . '" onclick="ins_image_v2(this.form.comment_text);" />';
			}

				$jsstr .= '<select name="style_dropdown" onchange="ins_style_dropdown(this.form.comment_text,this.form.style_dropdown.value);">
				<option label="--" value="--">--</option>';

                                $nsstr = "<noscript><p>Available Tags</p><ul>";
				foreach (array('blockquote','pre','code','strong', 'b', 'em', 'i', 'del', 'ins', 'strike') as $tag) {
					if ( in_array( $tag, $allowed ) ) {
						$jsstr .= "<option label=\"[$tag]xxx[/$tag]\" value=\"$tag\">[$tag]xxx[/$tag]</option>";
						$nsstr .= "<li>[$tag]xxx[/$tag]</li>";
					}
				}

				if ( in_array( 'hN', $allowed ) ) {
					$jsstr .= "<option label=\"[hN]xxx[/hN] (N=1-6)\" value=\"hN\">[hN]xxx[/hN] (?=1-6)</option>";
					$nsstr .= "<li>[hN]xxx[/hN] (N=1-6)</li>";
				}
				// TODO, this should really be hidden if the browser doesn't support javascript
				//print "<script type='text/javascript'>document.write('";
				print $jsstr . "</select>";
				//print "');<script>\n";

			print $nsstr . "</ul></noscript>";
			emoticons_show();

			if ( in_array( 'img', $allowed ) ) {
				global $theme_vars;
				?>
					<a traget="_blank" href='image_list.php'><?php echo( _sb('view_images') ); ?></a><br />
					<?php echo image_dropdown('comment_text'); ?><br /><br />
				<?php
			}
			?>

			<label for="comment_text"><?php echo( _sb('comment_text') ); ?></label><br />
			<textarea style="width: <?php global $theme_vars; echo( $theme_vars[ 'max_image_width' ] ); ?>px;" id="comment_text" name="comment_text" rows="20" cols="50" autocomplete="off"></textarea><br /><br />

			<?php
			if ($GLOBALS['logged_in']==true ) {
				echo('<!-- Logged in user -->');
				echo('<input type="hidden" name="comment_capcha" id="comment_capcha" value="' . $_SESSION[ 'capcha_' . $_GET[ 'entry' ] ] . '" autocomplete="off" maxlength="6" />');
			} else if ($blog_config->getTag('BLOG_ENABLE_CAPCHA')==0) {
				echo('<!-- Anti-spam disabled -->');
				echo('<input type="hidden" name="comment_capcha" id="comment_capcha" value="' . $_SESSION[ 'capcha_' . $_GET[ 'entry' ] ] . '" autocomplete="off" maxlength="6" />');
			} else {
				echo('<!-- Not logged in! Show capcha -->');
				echo('<label for="comment_capcha">');

				if ( function_exists('imagecreate') && $blog_config->getTag('BLOG_ENABLE_CAPCHA_IMAGE') ) {
					printf( _sb('comment_capcha') . ' <br /><img src="capcha.php?entry=' . $_GET[ 'entry' ] . '" />' );
				} else {
					printf( _sb('comment_capcha') . ' ' .sb_str_to_ascii( $_SESSION[ 'capcha_' . $_GET[ 'entry' ] ] ) );
				}

				echo('</label><br />');
				echo('<input type="text" name="comment_capcha" id="comment_capcha" value="" autocomplete="off" maxlength="6" /><br /><br />');

				if ( $blog_config->getTag('BLOG_COMMENTS_MODERATION') ) {
					if ( $logged_in == false ) {
						echo(_sb('user_notice_mod') . '<br /><br />');
					}
				}
			}
			?>

			<input type="submit" name="submit" value="<?php echo( _sb('post_btn') ); ?>" />

		</form>

		<?php
		// New 0.4.8
		$oBlacklist = new CBlacklist;
		$oBlacklist->load( CONFIG_DIR.'blacklist.txt' );
		if ( $oBlacklist->isBanned( getIP() ) == true ) {
			// Check Blacklist
			ob_end_clean();
			$entry_array[ 'entry' ] = _sb('blacklisted');
		} else if ( $blog_config->getTag('BLOG_ENABLE_COMMENTS') == false ) {
			ob_end_clean(); // Don't show anything to do with the comment editing.
			$entry_array[ 'entry' ] = _sb('nocomments'); 
		} else if ( are_comments_expired( $GLOBALS[ 'month' ], $GLOBALS[ 'day' ], $GLOBALS[ 'year' ] ) ) {
			// Check Expiration Date
			ob_end_clean();
			$entry_array[ 'entry' ] = _sb('expired_comment1') . $blog_config->getTag('BLOG_COMMENT_DAYS_EXPIRY') . _sb('expired_comment2');
		} else if ( $blog_config->getTag('BLOG_ENABLE_COMMENTS') == 0 ){
			ob_end_clean();
			$entry_array[ 'entry' ] = _sb('nocomments');
		} else {
			$entry_array[ 'entry' ] = ob_get_clean();
		}

		echo( theme_staticentry( $entry_array ) );
	}
	
	require_once(ROOT_DIR . '/scripts/sb_footer.php');
?>
