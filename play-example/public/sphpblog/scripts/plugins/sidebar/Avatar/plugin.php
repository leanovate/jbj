<?php
	/**
	* Avatar widget.
	*
	* sverde1 <sverde1@email.si>
	*/
	
	class Avatar extends Sidebar {
		var $plugin;
		
		/* ------ INITIALIZE ------ */
		
		function Avatar () {
			$this->plugin = 'Avatar';
			$this->loadPrefs();
		}
		
		/* ------ PREFERENCES ------ */
		
		function defaultPrefs () {
			$arr = parent::defaultPrefs();
			$arr['url'] = '';
			
			return $arr;
		}
		
		/* ------ OPTIONS ------ */
		
		function optionsForm () {
			$label_url = _sb('blog_avatar');
			$value_url = $this->prefs['url'];
			$save = _sb('submit_btn');
			$width = $GLOBALS['theme_vars']['max_image_width'] - 20;
		
			ob_start(); ?>
			<!-- FORM -->
			<form method="post" onsubmit="return validate(this)">
				<label for="url"><?php echo( $label_url ); ?></label><br />
				<input type="text" name="url" value="<?php echo( $value_url ); ?>" autocomplete="OFF" size="40" style="width: <?php echo( $width ); ?>px;"><p />
				
				<input type="submit" name="save" value="<?php echo( $save ); ?>" />
			</form>
			<?php
			$str = ob_get_clean();
		
			return $str;
		}
		
		function optionsPost () {	
			if ( array_key_exists( 'save', $_POST ) ) {
			
				if ( array_key_exists( 'url', $_POST ) ) {
					$url = sb_stripslashes( $_POST['url'] );
					if ( !empty( $url ) ) {
						$this->prefs['url'] = $url;
						$this->savePrefs();
					}
				}
				
			}
		}
		
		/* ------ GETTERS & SETTERS ------ */
		
		function getTitle () {
			return _sb('menu_avatar');
		}
		
		function getContent () {
			$str = '';
			
			if ( !empty( $this->prefs['url'] ) ) {
				$str = sprintf( '<img src="%s" alt="%s" />', $this->prefs['url'], _sb('menu_avatar'));
			}
			
			return $str;
		}
	}
?>
