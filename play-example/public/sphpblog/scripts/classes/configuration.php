<?php
	require_once("container.php");
	// TODO remove config.php dependency, pass in $this->path
	/**
	* Blog preferences.
	*
	* @author		Alexander Palmo <apalmo at bigevilbrain dot com>
	* @access		public
	*/
	class Configuration extends container {
		var $path, $tags;
		
		/**
		* Contructor
		*
		* @param		string $filename
		* @return		null
		*/
		function Configuration($filename='configuration.txt') {
			$this->path = CONFIG_DIR;
		
			$this->filename = $filename;
			
			$this->setDefaults();
		}
		
		function setDefaults() {
			// Allowed Tags
			$this->tags = array();
			
			array_push($this->tags, 'BLOG_TITLE');
			array_push($this->tags, 'BLOG_AUTHOR');
			array_push($this->tags, 'BLOG_FOOTER');
			array_push($this->tags, 'BLOG_LANGUAGE');
			array_push($this->tags, 'BLOG_ENTRY_ORDER');
			array_push($this->tags, 'BLOG_COMMENT_ORDER');
			array_push($this->tags, 'BLOG_ENABLE_COMMENTS');
			array_push($this->tags, 'BLOG_MAX_ENTRIES');
			array_push($this->tags, 'BLOG_COMMENTS_POPUP');
			array_push($this->tags, 'COMMENT_TAGS_ALLOWED');
			array_push($this->tags, 'BLOG_EMAIL');
			array_push($this->tags, 'BLOG_AVATAR');
			array_push($this->tags, 'BLOG_ENABLE_GZIP_TXT');
			array_push($this->tags, 'BLOG_ENABLE_GZIP_OUTPUT');
			array_push($this->tags, 'BLOG_EMAIL_NOTIFICATION');
			array_push($this->tags, 'BLOG_SEND_PINGS');
			array_push($this->tags, 'BLOG_PING_URLS');
			array_push($this->tags, 'BLOG_ENABLE_VOTING');
			array_push($this->tags, 'BLOG_TRACKBACK_ENABLED');
			array_push($this->tags, 'BLOG_TRACKBACK_AUTO_DISCOVERY');
			array_push($this->tags, 'BLOG_ENABLE_CACHE');
			array_push($this->tags, 'BLOG_ENABLE_CALENDAR');
			array_push($this->tags, 'BLOG_CALENDAR_START');
			array_push($this->tags, 'BLOG_ENABLE_TITLE');
			array_push($this->tags, 'BLOG_ENABLE_PERMALINK');
			array_push($this->tags, 'BLOG_ENABLE_STATS');
			array_push($this->tags, 'BLOG_ENABLE_LASTCOMMENTS');
			array_push($this->tags, 'BLOG_ENABLE_LASTENTRIES');
			array_push($this->tags, 'BLOG_ENABLE_CAPCHA');
			array_push($this->tags, 'BLOG_COMMENT_DAYS_EXPIRY');
			array_push($this->tags, 'BLOG_ENABLE_CAPCHA_IMAGE');
			array_push($this->tags, 'BLOG_ENABLE_ARCHIVES');
			array_push($this->tags, 'BLOG_ENABLE_LOGIN');
			array_push($this->tags, 'BLOG_ENABLE_COUNTER');
			array_push($this->tags, 'BLOG_FOOTER_COUNTER');
			array_push($this->tags, 'BLOG_COUNTER_HOURS');
			array_push($this->tags, 'BLOG_COMMENTS_MODERATION');
			array_push($this->tags, 'BLOG_SEARCH_TOP');
			array_push($this->tags, 'BLOG_ENABLE_STATIC_BLOCK');
			array_push($this->tags, 'STATIC_BLOCK_OPTIONS');
			array_push($this->tags, 'STATIC_BLOCK_BORDER');
			array_push($this->tags, 'BLOG_HEADER_GRAPHIC');
			array_push($this->tags, 'BLOG_ENABLE_START_CATEGORY');
			array_push($this->tags, 'BLOG_ENABLE_START_CATEGORY_SELECTION');
			array_push($this->tags, 'BLOG_ENABLE_PRINT');
			array_push($this->tags, 'INFO_KEYWORDS');
			array_push($this->tags, 'INFO_DESCRIPTION');
			array_push($this->tags, 'INFO_COPYRIGHT');
			array_push($this->tags, 'TRACKING_CODE');
			array_push($this->tags, 'BANNED_ADDRESS_LIST');
			array_push($this->tags, 'BANNED_WORD_LIST');
			array_push($this->tags, 'BLOG_THEME');
			array_push($this->tags, 'HTTPS');
			array_push($this->tags, 'HTTPS_URL');
			array_push($this->tags, 'RSS_MAX_ENTRIES');
			//array_push($this->tags, 'SHOW_COMMENTS');
			//array_push($this->tags, 'RATING_LOGIN');
			//array_push($this->tags, 'CAPCHA_RATING');
			array_push($this->tags, 'BACK_YEARS');
			array_push($this->tags, 'USE_EMOTICONS');
			array_push($this->tags, 'STATIC_HOME');
			array_push($this->tags, 'USE_JS_EDITOR');
			
			// Cache Excluded Tags
			// $this->do_not_cache_tags = array();
			// array_push($this->do_not_cache_tags, 'example');
			
			// Default Values
			foreach ($this->tags as $key) {
				$this->setTag($key, '');
			}

			$this->setTag('BLOG_TITLE', 'No Title');
			$this->setTag('BLOG_AUTHOR', 'No Author');
			$this->setTag('BLOG_FOOTER', 'No Footer');
			$this->setTag('BLOG_LANGUAGE', 'english');
			$this->setTag('BLOG_ENTRY_ORDER', 'new_to_old');
			$this->setTag('BLOG_COMMENT_ORDER', 'new_to_old');
			$this->setTag('BLOG_ENABLE_COMMENTS', 1);
			$this->setTag('BLOG_MAX_ENTRIES', 5);
			$this->setTag('BLOG_COMMENTS_POPUP', 0);
			$this->setTag('COMMENT_TAGS_ALLOWED', 'b,i,strong,em,url');
			$this->setTag('BLOG_EMAIL', 'email@myblog.com');
			// $this->setTag('BLOG_AVATAR', '');
			$this->setTag('BLOG_ENABLE_GZIP_TXT', 0);
			$this->setTag('BLOG_ENABLE_GZIP_OUTPUT', 0);
			$this->setTag('BLOG_EMAIL_NOTIFICATION', 0);
			$this->setTag('BLOG_SEND_PINGS', 0);
			// $this->setTag('BLOG_PING_URLS', '');
			$this->setTag('BLOG_ENABLE_VOTING', 1);
			$this->setTag('BLOG_TRACKBACK_ENABLED', 0);
			$this->setTag('BLOG_TRACKBACK_AUTO_DISCOVERY', 0);
			$this->setTag('BLOG_ENABLE_CACHE', 1);
			$this->setTag('BLOG_ENABLE_CALENDAR', 1);
			$this->setTag('BLOG_CALENDAR_START', 'sunday');
			$this->setTag('BLOG_ENABLE_TITLE', 1);
			$this->setTag('BLOG_ENABLE_PERMALINK', 1);
			$this->setTag('BLOG_ENABLE_STATS', 1);
			$this->setTag('BLOG_ENABLE_LASTCOMMENTS', 1);
			$this->setTag('BLOG_ENABLE_LASTENTRIES', 1);
			$this->setTag('BLOG_ENABLE_CAPCHA', 1);
			$this->setTag('BLOG_COMMENT_DAYS_EXPIRY', 0);
			$this->setTag('BLOG_ENABLE_CAPCHA_IMAGE', function_exists( 'imagecreate' ));
			$this->setTag('BLOG_ENABLE_ARCHIVES', 1);
			$this->setTag('BLOG_ENABLE_LOGIN', 1);
			$this->setTag('BLOG_ENABLE_COUNTER', 1);
			$this->setTag('BLOG_FOOTER_COUNTER', 1);
			$this->setTag('BLOG_COUNTER_HOURS', 24);
			$this->setTag('BLOG_COMMENTS_MODERATION', 0);
			$this->setTag('BLOG_SEARCH_TOP', 0);
			$this->setTag('BLOG_ENABLE_STATIC_BLOCK', 0);
			// $this->setTag('STATIC_BLOCK_OPTIONS', '');
			$this->setTag('STATIC_BLOCK_BORDER', 'border');
			// $this->setTag('BLOG_HEADER_GRAPHIC', '');
			$this->setTag('BLOG_ENABLE_START_CATEGORY', 0);
			// $this->setTag('BLOG_ENABLE_START_CATEGORY_SELECTION', '');
			$this->setTag('BLOG_ENABLE_PRINT', 0);
			// $this->setTag('INFO_KEYWORDS', '');
			// $this->setTag('INFO_DESCRIPTION', '');
			// $this->setTag('INFO_COPYRIGHT', '');
			$this->setTag('TRACKING_CODE', '');
			// $this->setTag('BANNED_ADDRESS_LIST', '');
			// $this->setTag('BANNED_WORD_LIST', '');
			$this->setTag('BLOG_THEME', 'default');
			$this->setTag('HTTPS', 'WARN');
			$this->setTag('HTTPS_URL', '');
			$this->setTag('RSS_MAX_ENTRIES', 10);
			//$this->setTag('SHOW_COMMENTS', 0);
			//$this->setTag('RATING_LOGIN', 0);
			//$this->setTag('CAPCHA_RATING', 0);
			$this->setTag('BACK_YEARS', 10);
			$this->setTag('USE_EMOTICONS', 1);
			$this->setTag('STATIC_HOME', '');
			$this->setTag('USE_JS_EDITOR', 1);
		}
		
		function write_file() {
			$result = parent::write_file();
			return $result;
		}
	}
?>
