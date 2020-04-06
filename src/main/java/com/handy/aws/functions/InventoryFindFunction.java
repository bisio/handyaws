package com.handy.aws.functions;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.google.gson.Gson;

import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;

public class InventoryFindFunction implements RequestHandler<HttpQuerystringRequest, String> {

    @Override
    public String handleRequest(HttpQuerystringRequest request, Context context) {
        context.getLogger().log("Input: " + request);
        String id = request.getQueryStringParameters().get("id");
        return getProductById(Integer.parseInt(id)).toString();
    }

	private Product getProductById(int prodId) {
		Region  region = Region.EU_WEST_1;
        S3Client s3Client = S3Client.builder().region(region).build();
        ResponseInputStream<?> objectData = s3Client.getObject(GetObjectRequest.builder()
        		.bucket("handy-inventory-data-bisio")
        		.key("handy-tool-catalog.json")
        		.build());
        InputStreamReader isr = new InputStreamReader(objectData);
        BufferedReader br = new BufferedReader(isr);

        Product[] products = null;
        Gson gson = new Gson();
        
        products = gson.fromJson(br, Product[].class);
        
        for (Product prod : products) {
        	if (prod.getId() == prodId) {
        		return prod;
        	}
        }
        return null;
	}
}
