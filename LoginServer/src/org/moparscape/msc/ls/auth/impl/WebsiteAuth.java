package org.moparscape.msc.ls.auth.impl;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import org.moparscape.msc.ls.auth.Auth;
import org.moparscape.msc.ls.util.Config;
import org.moparscape.msc.ls.util.DataConversions;

class WebsiteAuth implements Auth {

	private final double version = 1.0;

	public boolean validate(long hash, byte[] passBA, StringBuilder response) {
		String user = DataConversions.hashToUsername(hash);
		// if authURL is null, then we are just running the server for test
		// purposes
		// this will never be so in production
		if (Config.AUTH_META_DATA == null) {
			response.append("TestUser");
			return true;
		}
		try {
			user = URLEncoder.encode(user, "UTF-8");
			String pass = URLEncoder.encode(new String(passBA, "UTF-8"),
					"UTF-8");

			HttpURLConnection.setFollowRedirects(false);
			HttpURLConnection uc = (HttpURLConnection) new URL(
					Config.AUTH_META_DATA).openConnection();

			uc.setRequestMethod("POST");
			uc.setDoInput(true);
			uc.setDoOutput(true);
			uc.setUseCaches(false);
			uc.setAllowUserInteraction(false);
			uc.setInstanceFollowRedirects(false);
			uc.setRequestProperty("User-Agent", "Mozilla/5.0 MoparClassic/"
					+ version);
			uc.setRequestProperty("Content-Type",
					"application/x-www-form-urlencoded");
			DataOutputStream out = new DataOutputStream(uc.getOutputStream());
			out.writeBytes("user=" + user + "&pass=" + pass);
			out.flush();
			out.close();

			BufferedReader in = new BufferedReader(new InputStreamReader(
					uc.getInputStream()));
			String line = in.readLine();
			boolean success = line != null && line.equals("YES");
			response.append(in.readLine());
			in.close();
			return success;
		} catch (Exception e) {
			response.append(e.getMessage());
			return false;
		}
	}
}