package org.moparscape.msc.ls.auth;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import org.moparscape.msc.ls.util.Config;

public class Auth {

	public static double version = 1.0;

    public static boolean check_auth(String user, String pass, StringBuilder response) {
        // if authURL is null, then we are just running the server for test purposes
        // this will never be so in production
        if(Config.AUTH_URL == null){
            response.append("TestUser");
            return true;
        }
        try {
            user = URLEncoder.encode(user, "UTF-8");
            pass = URLEncoder.encode(pass, "UTF-8");

            HttpURLConnection.setFollowRedirects(false);
            HttpURLConnection uc = (HttpURLConnection) new URL(Config.AUTH_URL).openConnection();

            uc.setRequestMethod("POST");
            uc.setDoInput(true);
            uc.setDoOutput(true);
            uc.setUseCaches(false);
            uc.setAllowUserInteraction(false);
            uc.setInstanceFollowRedirects(false);
            uc.setRequestProperty("User-Agent", "Mozilla/5.0 MoparClassic/" + version);
            uc.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            DataOutputStream out = new DataOutputStream(uc.getOutputStream());
            out.writeBytes("user=" + user + "&pass=" + pass);
            out.flush();
            out.close();

            BufferedReader in = new BufferedReader(new InputStreamReader(uc.getInputStream()));
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

    public static void main(String[] args){
    	Config.AUTH_URL = "https://www.moparscape.org/auth.php?field=";
        String user = "CodeForFame";
        String pass = "";
        StringBuilder sb = new StringBuilder();
        System.out.println("success: "+check_auth(user, pass, sb));
        System.out.println("message: "+sb.toString());
    }
}