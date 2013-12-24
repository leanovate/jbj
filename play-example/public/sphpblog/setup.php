<?php
	// ---------------
	// INITIALIZE PAGE
	// ---------------
	require_once('scripts/sb_functions.php');
	global $logged_in, $blog_config;
	$logged_in = logged_in( true, true );

	$page_title = _sb('setup_title');
	require_once('scripts/sb_header.php');

	// ---------------
	// POST PROCESSING
	// ---------------
	

	// ------------
	// PAGE CONTENT
	// ------------
	function page_content() {
		global $user_colors, $logged_in, $theme_vars, $blog_theme, $blog_config;
		
		// SUBJECT
		$entry_array = array();
		$entry_array[ 'subject' ] = _sb('setup_title');
		
		$allowed = explode(',', $blog_config->getTag('COMMENT_TAGS_ALLOWED'));
		
		// PAGE CONTENT BEGIN
		ob_start(); ?>
		<?php echo( _sb('setup_instructions') ); ?><p />

		<!-- FORM -->
		<form action="setup_cgi.php" method="post" name="setup" name="setup">

			<!-- LANGUAGE -->
			<a id="linkSetupLanguage" href="javascript:toggleBlock('SetupLanguage');">
				<img src="<?php global $blog_theme; $img_hide = 'themes/' . $blog_theme . '/images/minus.gif'; echo( $img_hide ); ?>" name="twisty">
				<h4 style="display: inline;"><?php echo( _sb('title_language') ); ?></h4>
			</a><p />
			<div id="toggleSetupLanguage"><p />
				<?php
					$arr = array();
					$dir = 'languages/';

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
										$item = array();
										$item['label'] = $lang_name;
										$item['value'] = $lang_dir;
										if ( $blog_config->getTag('BLOG_LANGUAGE') == $item['value'] ) {
											$item['selected'] = true;
										}
										array_push( $arr, $item );
									}
								}
								$sub_dir = readdir( $dhandle );
							}
						}
						closedir( $dhandle );
					}

					// sort array
					// Obtain a list of columns
					$label = array();
					for ($i = 0; $i < sizeof($arr); $i++) {
						$label[] = $arr[$i]['label'];
					}
                                        array_multisort($label, $arr);
					// HTML_dropdown( $label=false, $id, $itemArray, $add_returns=true, $onchange=null, $width=0, $size=0, $multiple=false, $disabled=false )
					echo( HTML_dropdown( _sb('blog_choose_language'), "blog_language", $arr ) );
				?>
			</div>

			<p />

			<!-- GENERAL -->
			<a id="linkSetupGeneral" href="javascript:toggleBlock('SetupGeneral');">
				<img src="<?php global $blog_theme; $img_hide = 'themes/' . $blog_theme . '/images/minus.gif'; echo( $img_hide ); ?>" name="twisty">
				<h4 style="display: inline;"><?php echo( _sb('title_general') ); ?></h4>
			</a><p />
			<div id="toggleSetupGeneral" class="toggleBody"><p />
				<input type="checkbox" id="blog_enable_title" name="blog_enable_title"<?php if ( $blog_config->getTag('BLOG_ENABLE_TITLE') == 1 ) { echo ( ' checked' ); } ?>> <?php echo( _sb('blog_enable_title') ); ?><p />

				<label for="blog_header_graphic"><?php echo( _sb('blog_header') ); ?></label><br />
				<input type="text" name="blog_header_graphic" value="<?php echo($blog_config->getTag('BLOG_HEADER_GRAPHIC')); ?>" autocomplete="OFF" size="40" style="width: <?php global $theme_vars; echo( $theme_vars[ 'max_image_width' ] - 20 ); ?>px;"><p />

				<label for="blog_title"><?php echo( _sb('blog_title') ); ?></label><br />
				<input type="text" name="blog_title" value="<?php echo($blog_config->getTag('BLOG_TITLE')); ?>" autocomplete="OFF" size="40" style="width: <?php global $theme_vars; echo( $theme_vars[ 'max_image_width' ] - 20 ); ?>px;"><p />

				<label for="blog_author"><?php echo( _sb('blog_author') ); ?></label><br />
				<input type="text" name="blog_author" value="<?php echo($blog_config->getTag('BLOG_AUTHOR')); ?>" autocomplete="OFF" size="40" style="width: <?php global $theme_vars; echo( $theme_vars[ 'max_image_width' ] - 20 ); ?>px;"><p />

				<label for="blog_email"><?php echo( _sb('blog_email') ); ?></label><br />
				<input type="text" name="blog_email" value="<?php echo($blog_config->getTag('BLOG_EMAIL')); ?>" autocomplete="OFF" size="40" style="width: <?php global $theme_vars; echo( $theme_vars[ 'max_image_width' ] - 20 ); ?>px;"><p />

				<label for="blog_avatar"><?php echo( _sb('blog_avatar') ); ?></label><br />
				<input type="text" name="blog_avatar" value="<?php echo($blog_config->getTag('BLOG_AVATAR')); ?>" autocomplete="OFF" size="40" style="width: <?php global $theme_vars; echo( $theme_vars[ 'max_image_width' ] - 20 ); ?>px;"><p />

				<label for="blog_footer"><?php echo( _sb('blog_footer') ); ?></label><br />
				<input type="text" name="blog_footer" value="<?php echo($blog_config->getTag('BLOG_FOOTER')); ?>" autocomplete="OFF" size="40" style="width: <?php global $theme_vars; echo( $theme_vars[ 'max_image_width' ] - 20 ); ?>px;"><p />

				<p>
	                        <label for="https">HTTPS Login:</label>
        	                <select name="https" id="https">
                	                <option label="NONE" value="NONE"<?php if ($blog_config->getTag('HTTPS') == 'NONE') { echo ' selected'; } ?>>NONE</option>
                	                <option label="WARN" value="WARN"<?php if ($blog_config->getTag('HTTPS') == 'WARN') { echo ' selected'; } ?>>WARN</option>
                        	        <option label="REQUIRE" value="REQUIRE"<?php if ($blog_config->getTag('HTTPS') == 'REQUIRE') { echo ' selected'; } ?>>REQUIRE</option>
                        	</select>
				</p>
				<p>
	                        <label for="https_url">HTTPS Login URL:</label>
				<input type="text" name="https_url" value="<?php echo($blog_config->getTag('HTTPS_URL')); ?>" size="40" style="width: <?php global $theme_vars; echo( $theme_vars[ 'max_image_width' ] - 20 ); ?>px;">
				</p>
				<p>
				<input type="checkbox" id="use_emoticons" name="use_emoticons"<?php if ( $blog_config->getTag('USE_EMOTICONS') == 1 ) { echo ( ' checked' ); } ?>> 
				<?php print _sb("Enable Emoticons"); ?>
				</p>
				<p>
				<input type="checkbox" id="use_js_editor" name="use_js_editor"<?php if ( $blog_config->getTag('USE_JS_EDITOR') == 1 ) { echo ( ' checked' ); } ?>> 
				<?php print _sb("Enable Advanced Editor"); ?>
				</p>
			</div>

			<p />

			<!-- ENTRIES -->
			<a id="linkSetupEntries" href="javascript:toggleBlock('SetupEntries');">
				<img src="<?php global $blog_theme; $img_hide = 'themes/' . $blog_theme . '/images/minus.gif'; echo( $img_hide ); ?>" name="twisty">
				<h4 style="display: inline;"><?php echo( _sb('title_entries') ); ?></h4>
			</a><p />
			<div id="toggleSetupEntries" class="toggleBody"><p />
				<input type="checkbox" id="blog_enable_voting" name="blog_enable_voting"<?php if ( $blog_config->getTag('BLOG_ENABLE_VOTING') == 1 ) { echo ( ' checked' ); } ?>> <?php echo( _sb('blog_enable_voting') ); ?><br />
				<input type="checkbox" id="blog_enable_permalink" name="blog_enable_permalink"<?php if ( $blog_config->getTag('BLOG_ENABLE_PERMALINK') == 1 ) { echo ( ' checked' ); } ?>> <?php echo( _sb('blog_enable_permalink') ); ?><br />
				<!--<input type="checkbox" id="blog_enable_print" name="blog_enable_print"<?php if ( $blog_config->getTag('BLOG_ENABLE_PRINT') == 1 ) { echo ( ' checked' ); } ?>> <?php echo( _sb('blog_enable_print') ); ?><br />-->
				<!--<input type="checkbox" id="blog_enable_print" name="blog_enable_print" disabled="disabled"> <?php echo( _sb('blog_enable_print') ); ?> (DEPRECATED)<br />-->
				<input type="checkbox" id="blog_enable_capcha" name="blog_enable_capcha"<?php if ( $blog_config->getTag('BLOG_ENABLE_CAPCHA') == 1 ) { echo ( ' checked' ); } ?>> <?php echo( _sb('blog_enable_capcha') ); ?><br />
				&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<input type="checkbox" id="blog_enable_capcha_image" name="blog_enable_capcha_image"<?php if ( $blog_config->getTag('BLOG_ENABLE_CAPCHA_IMAGE') == 1 ) { echo ( ' checked' ); } if ( function_exists( 'imagecreate' )==false ) { echo(' disabled'); }?>> <?php echo( _sb('blog_enable_capcha_image') ); ?><br />
				<input type="checkbox" id="blog_enable_cache" name="blog_enable_cache"<?php if ( $blog_config->getTag('BLOG_ENABLE_CACHE') == 1 ) { echo ( ' checked' ); } ?>> <?php echo( _sb('blog_enable_cache') ); ?><br />
				<input type="checkbox" id="blog_send_pings" name="blog_send_pings"<?php if ( $blog_config->getTag('BLOG_SEND_PINGS') == 1 ) { echo ( ' checked' ); } ?>> <?php echo( _sb('blog_send_pings') ); ?><p />
				<label for="blog_ping_urls"><?php echo( _sb('blog_ping_urls') ); ?></label><br />
				<input type="text" name="blog_ping_urls" value="<?php echo($blog_config->getTag('BLOG_PING_URLS')); ?>" autocomplete="OFF" size="40" style="width: <?php global $theme_vars; echo( $theme_vars[ 'max_image_width' ] - 20 ); ?>px;"><br />
				<br />
				<label for="blog_max_entries"><?php echo( _sb('blog_max_entries') ); ?></label><br />
				<input type="text" name="blog_max_entries" value="<?php echo( $blog_config->getTag('BLOG_MAX_ENTRIES') ); ?>" autocomplete="OFF" size="6">
				<br />
				<label for="rss_max_entries"><?php echo( _sb('rss_max_entries') ); ?></label><br />
				<input type="text" name="rss_max_entries" value="<?php echo( $blog_config->getTag('RSS_MAX_ENTRIES') ); ?>" autocomplete="OFF" size="6">
				<br />
				<label for="back_years"><?php print _sb("Allowed number of years to go back.  WARNING: You may get unexpected behavior when using years before 2000."); ?></label><br />
				<input type="text" name="back_years" value="<?php echo( $blog_config->getTag('BACK_YEARS') ); ?>" autocomplete="OFF" size="6">
				<p />
				<?php
					// Entry Order Drop Down
					$arr = array();
					array_push( $arr, array( 'label' => _sb('select_new_to_old'), 'value' => 'new_to_old', 'selected' => $blog_config->getTag('BLOG_ENTRY_ORDER') == 'new_to_old' ) );
					array_push( $arr, array( 'label' => _sb('select_old_to_new'), 'value' => 'old_to_new', 'selected' => $blog_config->getTag('BLOG_ENTRY_ORDER') == 'old_to_new' ) );
					echo( HTML_dropdown( _sb('label_entry_order'), "blog_entry_order", $arr ) );
				?>
				<?php
					// Comment Order Drop Down
					$arr = array();
					array_push( $arr, array( 'label' => _sb('select_new_to_old'), 'value' => 'new_to_old', 'selected' => $blog_config->getTag('BLOG_COMMENT_ORDER') == 'new_to_old' ) );
					array_push( $arr, array( 'label' => _sb('select_old_to_new'), 'value' => 'old_to_new', 'selected' => $blog_config->getTag('BLOG_COMMENT_ORDER') == 'old_to_new' ) );
					echo( HTML_dropdown( _sb('label_comment_order'), "blog_comment_order", $arr ) );
				?>
				<input type="checkbox" id="blog_enable_start_category" name="blog_enable_start_category"<?php if ( $blog_config->getTag('BLOG_ENABLE_START_CATEGORY') == 1 ) { echo ( ' checked' ); } ?>> <?php echo( _sb('blog_enable_start_category') ); ?>
					<?php
						category_combo_box( 'blog_enable_start_category_selection', intval($blog_config->getTag('BLOG_ENABLE_START_CATEGORY_SELECTION')) );							
					?>
				</select></br>
				<label for="static_home"><?php echo _sb("Use static page for first page:") ?></label>
				<select id="static_home" name="static_home">
				<option value="">--No--</option>
					<?php
                                // Saved User Colors
                                $dir = CONTENT_DIR.'static/';
                                $static_files = sb_folder_listing( $dir, array( '.txt', '.gz' ) );
                                if ( count( $static_files ) > 0 ) {
                                        for ( $i = 0; $i < count( $static_files ); $i++ ) {
                                                $filename = sb_strip_extension( $static_files[$i] );
                                                $str = '<option label="' . $filename . '" value="' . $filename . '"';
						if ($blog_config->getTag('STATIC_HOME') == $filename) {
							$str .= " selected='selected'";
						}
                                                $str    .= '>' . $filename . '</option>' . "\n";
                                                echo( $str );
                                        }
                                }
					?>
				</select></br>
			</div>

			<p />

			<!-- SIDEBAR -->
			<a id="linkSetupSidebar" href="javascript:toggleBlock('SetupSidebar');">
				<img src="<?php global $blog_theme; $img_hide = 'themes/' . $blog_theme . '/images/minus.gif'; echo( $img_hide ); ?>" name="twisty">
				<h4 style="display: inline;"><?php echo( _sb('title_sidebar') ); ?></h4>
			</a><p />
			<div id="toggleSetupSidebar" class="toggleBody"><p />
				<input type="checkbox" id="blog_enable_login" name="blog_enable_login"<?php if ( $blog_config->getTag('BLOG_ENABLE_LOGIN') == 1 ) { echo ( ' checked' ); } ?>> <?php echo( _sb('blog_enable_login') ); ?><br />
				<input type="checkbox" id="blog_enable_stats" name="blog_enable_stats"<?php if ( $blog_config->getTag('BLOG_ENABLE_STATS') == 1 ) { echo ( ' checked' ); } ?>> <?php echo( _sb('blog_enable_stats') ); ?><br />
				<input type="checkbox" id="blog_enable_lastcomments" name="blog_enable_lastcomments"<?php if ( $blog_config->getTag('BLOG_ENABLE_LASTCOMMENTS') == 1 ) { echo ( ' checked' ); } ?>> <?php echo( _sb('blog_enable_lastcomments') ); ?><br />
				<input type="checkbox" id="blog_enable_lastentries" name="blog_enable_lastentries"<?php if ( $blog_config->getTag('BLOG_ENABLE_LASTENTRIES') == 1 ) { echo ( ' checked' ); } ?>> <?php echo( _sb('blog_enable_lastentries') ); ?><br />
				<input type="checkbox" id="blog_enable_archives" name="blog_enable_archives"<?php if ( $blog_config->getTag('BLOG_ENABLE_ARCHIVES') == 1 ) { echo ( ' checked' ); } ?>> <?php echo( _sb('blog_enable_archives') ); ?><br />
				<input type="checkbox" id="blog_footer_counter" name="blog_footer_counter"<?php if ( $blog_config->getTag('BLOG_FOOTER_COUNTER') == 1 ) { echo ( ' checked' ); } ?>> <?php echo( _sb('blog_footer_counter') ); ?><br />
				<input type="checkbox" id="blog_enable_counter" name="blog_enable_counter"<?php if ( $blog_config->getTag('BLOG_ENABLE_COUNTER') == 1 ) { echo ( ' checked' ); } ?>> <?php echo( _sb('blog_enable_counter') ); ?><br />
				<label for="blog_counter_hours"><?php echo( _sb('blog_counter_hours') ); ?></label>
				<input type="text" name="blog_counter_hours" value="<?php echo( $blog_config->getTag('BLOG_COUNTER_HOURS') ); ?>" autocomplete="OFF" size="3"><p />
				<input type="checkbox" id="blog_enable_calendar" name="blog_enable_calendar"<?php if ( $blog_config->getTag('BLOG_ENABLE_CALENDAR') == 1 ) { echo ( ' checked' ); } ?>> <?php echo( _sb('blog_enable_calendar') ); ?><br />
				<label for="blog_calendar_start"><?php echo( _sb('label_calendar_start') ); ?></label>
				<select name="blog_calendar_start">
					<option label="<?php echo( _sb('cal_sunday') ); ?>" value="sunday"<?php if ($blog_config->getTag('BLOG_CALENDAR_START') == 'sunday') { echo ' selected'; } ?>><?php echo( _sb('cal_sunday') ); ?></option>
					<option label="<?php echo( _sb('cal_monday') ); ?>" value="monday"<?php if ($blog_config->getTag('BLOG_CALENDAR_START') == 'monday') { echo ' selected'; } ?>><?php echo( _sb('cal_monday') ); ?></option>
				</select><p />
				<input type="checkbox" id="blog_search_top" name="blog_search_top"<?php if ( $blog_config->getTag('BLOG_SEARCH_TOP') == 1 ) { echo ( ' checked' ); } ?>> <?php echo( _sb('blog_search_top') ); ?><br />
				<input type="checkbox" id="blog_enable_static_block" name="blog_enable_static_block"<?php if ( $blog_config->getTag('BLOG_ENABLE_STATIC_BLOCK') == 1 ) { echo ( ' checked' ); } ?>> <?php echo( _sb('blog_enable_static_block') ); ?>
				<br />&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
				<select name="static_block_options">
					<?php
						$blocktitles = get_block_list();
						for ( $i = 0; $i < count( $blocktitles ); $i+=2 ) {
						if ( isset( $blocktitles[ $i ] ) ) {
							echo( '<option label="' . $blocktitles[ $i ] . '" value="' . $blocktitles[ $i ] . '"' );
								if ( $blog_config->getTag('STATIC_BLOCK_OPTIONS') == $blocktitles[ $i ] ) {
									echo( ' selected>' );
								} else {
									echo( '>' ); }
								echo( $blocktitles[ $i ] . '</option>' );
							}
						}
					?>
				</select>
				<select name="static_block_border">
					<option label="<?php echo( _sb('static_block_border') ); ?>" value="border"<?php if ($blog_config->getTag('STATIC_BLOCK_BORDER') == 'border') { echo ' selected'; } ?>><?php echo( _sb('static_block_border') ); ?></option>
					<option label="<?php echo( _sb('static_block_noborder') ); ?>" value="noborder"<?php if ($blog_config->getTag('STATIC_BLOCK_BORDER') == 'noborder') { echo ' selected'; } ?>><?php echo( _sb('static_block_noborder') ); ?></option>
				</select><br />

			</div>

			<p />

			<!-- COMMENTS -->
			<a id="linkSetupComments" href="javascript:toggleBlock('SetupComments');">
				<img src="<?php global $blog_theme; $img_hide = 'themes/' . $blog_theme . '/images/minus.gif'; echo( $img_hide ); ?>" name="twisty">
				<h4 style="display: inline;"><?php echo( _sb('title_comments') ); ?></h4>
			</a><p />
			<div id="toggleSetupComments" class="toggleBody"><p />
				<input type="checkbox" id="blog_enable_comments" name="blog_enable_comments"<?php if ( $blog_config->getTag('BLOG_ENABLE_COMMENTS') == 1 ) { echo ( ' checked' ); } ?>> <?php echo( _sb('blog_enable_comments') ); ?><br />
				<input type="checkbox" id="blog_email_notification" name="blog_email_notification"<?php if ( $blog_config->getTag('BLOG_EMAIL_NOTIFICATION') == 1 ) { echo ( ' checked' ); } ?>> <?php echo( _sb('blog_email_notification') ); ?><br />
				<label for="blog_comment_days_expiry"><?php echo( _sb('blog_comment_days_expiry') ); ?></label>
				<input type="text" name="blog_comment_days_expiry" value="<?php echo( $blog_config->getTag('BLOG_COMMENT_DAYS_EXPIRY') ); ?>" autocomplete="OFF" size="3"><p />

				<label for="area"><?php echo( _sb('blog_comment_tags') ); ?></label><br />

				<table border="0" width="<?php global $theme_vars; echo( $theme_vars[ 'max_image_width' ] - 20 ); ?>" cellspacing="0" cellpadding="0">
					<tr align="left" valign="top">
						<td width="50%">
							<input type="checkbox" id="b" name="b"<?php if ( in_array( 'b', $allowed ) ) { echo ( ' checked' ); } ?>> [b] = Bold<br />
							<input type="checkbox" id="i" name="i"<?php if ( in_array( 'i', $allowed ) ) { echo ( ' checked' ); } ?>> [i] = Italic<br />
							<input type="checkbox" id="strong" name="strong"<?php if ( in_array( 'strong', $allowed ) ) { echo ( ' checked' ); } ?>> [strong] = Strong<br />
							<input type="checkbox" id="center" name="center"<?php if ( in_array( 'center', $allowed ) ) { echo ( ' checked' ); } ?>> [center] = Center<br />
							<input type="checkbox" id="em" name="em"<?php if ( in_array( 'em', $allowed ) ) { echo ( ' checked' ); } ?>> [em] = Emphasis<br />
							<br />
							<input type="checkbox" id="del" name="del"<?php if ( in_array( 'del', $allowed ) ) { echo ( ' checked' ); } ?>> [del] = Delete<br />
							<input type="checkbox" id="ins" name="ins"<?php if ( in_array( 'ins', $allowed ) ) { echo ( ' checked' ); } ?>> [ins] = Insert<br />
							<input type="checkbox" id="strike" name="strike"<?php if ( in_array( 'strike', $allowed ) ) { echo ( ' checked' ); } ?>> [strike] = Strike-out<br />
						</td>
						<td width="50%">
							<!--<input type="checkbox" id="img" name="img"<?php if ( in_array( 'img', $allowed ) ) { echo ( ' checked' ); } ?>> [img] = Image<br />-->
							<input type="checkbox" id="url" name="url"<?php if ( in_array( 'url', $allowed ) ) { echo ( ' checked' ); } ?>> [url] = URL<br />
							<br />
							<input type="checkbox" id="blockquote" name="blockquote"<?php if ( in_array( 'blockquote', $allowed ) ) { echo ( ' checked' ); } ?>> [blockquote] = Block Quote<br />
							<input type="checkbox" id="hN" name="hN"<?php if ( in_array( 'hN', $allowed ) ) { echo ( ' checked' ); } ?>> [hN] = Headline<br />
							<br />
							<input type="checkbox" id="pre" name="pre"<?php if ( in_array( 'pre', $allowed ) ) { echo ( ' checked' ); } ?>> [pre] = Preformatted<br />
							<input type="checkbox" id="code" name="code"<?php if ( in_array( 'code', $allowed ) ) { echo ( ' checked' ); } ?>> [code] = Code<br />
<!--							<input type="checkbox" id="html" name="html"<?php if ( in_array( 'html', $allowed ) ) { echo ( ' checked' ); } ?>> [html] = HTML<br />-->
						</td>
					</tr>
				</table><p />
				<label for="area"><b><?php echo( _sb('comment_moderation') ); ?></b></label><br />
				<input type="checkbox" id="blog_comments_moderation" name="blog_comments_moderation"<?php if ( $blog_config->getTag('BLOG_COMMENTS_MODERATION') == 1 ) { echo ( ' checked' ); } ?>> <?php echo( _sb('blog_comments_moderation') ); ?><p />

			</div>

			<p />

			<!-- COMPRESSION -->
			<a id="linkSetupCompression" href="javascript:toggleBlock('SetupCompression');">
				<img src="<?php global $blog_theme; $img_hide = 'themes/' . $blog_theme . '/images/minus.gif'; echo( $img_hide ); ?>" name="twisty">
				<h4 style="display: inline;"><?php echo( _sb('title_compression') ); ?></h4>
			</a><p />
			<div id="toggleSetupCompression" class="toggleBody"><p />
				<?php echo( _sb('blog_gzip_about') ); ?><p />
				<!--<input type="checkbox" id="blog_enable_gzip_txt" name="blog_enable_gzip_txt"<?php if ( $blog_config->getTag('BLOG_ENABLE_GZIP_TXT') == 1 ) { echo ( ' checked' ); } if ( extension_loaded( 'zlib' ) == false ) { echo ( ' disabled' ); } ?>> <?php echo( _sb('blog_enable_gzip_txt') ); ?><br />-->
				<!--<input type="checkbox" id="blog_enable_gzip_txt" name="blog_enable_gzip_txt" disabled="disabled" <?php if ( $blog_config->getTag('BLOG_ENABLE_GZIP_TXT') == 1 ) { echo ( ' checked' ); } if ( extension_loaded( 'zlib' ) == false ) { echo ( ' disabled' ); } ?>> <?php echo( _sb('blog_enable_gzip_txt') ); ?> (DEPRECATED)<br />-->
				<input type="checkbox" id="blog_enable_gzip_output" name="blog_enable_gzip_output"<?php if ( $blog_config->getTag('BLOG_ENABLE_GZIP_OUTPUT') == 1 ) { echo ( ' checked' ); } if ( extension_loaded( 'zlib' ) == false ) { echo ( ' disabled' ); } ?>> <?php echo( _sb('blog_enable_gzip_output') ); ?><p />
			</div>

			<p />

			<!-- SUBMIT -->
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
