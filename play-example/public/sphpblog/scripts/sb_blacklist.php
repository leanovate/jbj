<?php
	/* @module file="sb_blacklist.php" version="Rev0.1.0" date="20060306" */

	/*---------------------------------------------------------------------*/

	class CBlacklist {
		//-----------------------------------------------------------------
		var $aEntries;
		//-----------------------------------------------------------------
		function CBlacklist( ) {
			$this->aEntries = array( );
		}
		//-----------------------------------------------------------------

		function load( $sBlacklistPathFileName  ) {
			$bReturnCode = false;
			
			$sContent = $GLOBALS['blog_config']->getTag('BANNED_ADDRESS_LIST'); // sb_read_file( $sBlacklistPathFileName );
			if ( $sContent !== false ) {
				$this->aEntries = explode( chr(13), trim( $sContent ) );
				$bReturnCode = true;
			}

			return $bReturnCode;
		}

		function isBanned( $sIPAddress ) {
			return in_array( $sIPAddress, $this->aEntries );
		}
	}

	/*---------------------------------------------------------------------*/

	class CBannedWords {
		//-----------------------------------------------------------------
		var $aEntries;
		//-----------------------------------------------------------------
		function CBannedWords( ) {
			$this->aEntries = array( );
		}
		//-----------------------------------------------------------------

		function load( $sBannedWordsPathFileName  ) {
			$bReturnCode = false;

			$sContent = $GLOBALS['blog_config']->getTag('BANNED_WORD_LIST'); // sb_read_file( $sBannedWordsPathFileName );
			if ( $sContent !== false ) {
				$this->aEntries = explode( chr(13), trim( $sContent ) );
				$bReturnCode = true;
			}

			return $bReturnCode;
		}

		function ContainsBannedWord( $author, $email, $url, $comment ) {
			foreach($this->aEntries as $word) {
				$word = trim($word);
				$pattern = "#$word#i";
				if ('##i' != $pattern) { // no blank lines allowed
					if ( preg_match($pattern, $author) ) return true;
					if ( preg_match($pattern, $email) ) return true;
					if ( preg_match($pattern, $url) ) return true;
					if ( preg_match($pattern, $comment) ) return true;
				}

			}

			return false;
		}
	}
?>
