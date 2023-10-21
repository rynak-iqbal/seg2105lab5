package mgarzon.createbest.productcatalog;

import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;

import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    EditText editTextName;
    EditText editTextPrice;
    Button buttonAddProduct;
    ListView listViewProducts;

    List<Product> products;

    DatabaseReference databaseProducts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        databaseProducts = FirebaseDatabase.getInstance().getReference("products");
        
        editTextName = (EditText) findViewById(R.id.editTextName);
        editTextPrice = (EditText) findViewById(R.id.editTextPrice);
        listViewProducts = (ListView) findViewById(R.id.listViewProducts);
        buttonAddProduct = (Button) findViewById(R.id.addButton);

        products = new ArrayList<>();

        //adding an onclicklistener to button
        buttonAddProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addProduct();
            }
        });

        listViewProducts.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                Product product = products.get(i);
                showUpdateDeleteDialog(product.getId(), product.getProductName());
                return true;
            }
        });
    }


    @Override
    protected void onStart() {
        super.onStart();

        databaseProducts.addValueEventListener(new ValueEventListener(){
            @Override
            public void onDataChange(DataSnapshot dataSnapshot){
                products.clear();

                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    Product product = postSnapshot.getValue(Product.class);
                    products.add(product);
                }

                ProductList adapter = new ProductList(MainActivity.this, products);
                ListViewProducts.setAdapter(adapter);
                
             }
            @Override
            public void onCancelled(DatabaseError databaseError){
                
            }
        });
    }


    private void showUpdateDeleteDialog(final String productId, String productName) {

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.update_dialog, null);
        dialogBuilder.setView(dialogView);

        final EditText editTextName = (EditText) dialogView.findViewById(R.id.editTextName);
        final EditText editTextPrice  = (EditText) dialogView.findViewById(R.id.editTextPrice);
        final Button buttonUpdate = (Button) dialogView.findViewById(R.id.buttonUpdateProduct);
        final Button buttonDelete = (Button) dialogView.findViewById(R.id.buttonDeleteProduct);

        dialogBuilder.setTitle(productName);
        final AlertDialog b = dialogBuilder.create();
        b.show();

        buttonUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = editTextName.getText().toString().trim();
                double price = Double.parseDouble(String.valueOf(editTextPrice.getText().toString()));
                if (!TextUtils.isEmpty(name)) {
                    updateProduct(productId, name, price);
                    b.dismiss();
                }
            }
        });

        buttonDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteProduct(productId);
                b.dismiss();
            }
        });
    }

   private void updateProduct(String id, String name, double price) {

        DatabaseReference dr = FirebaseDatabase.getInstance().getReference("products").child(id);
        Product product = new Product(id, name, price);
        dr.setValue(product);
        Toast.makeText(getApplicationContext(), "Product Updated", Toast.LENGTH_LONG).show();
    }

    private void deleteProduct(String id) {

        Toast.makeText(getApplicationContext(), "NOT IMPLEMENTED YET", Toast.LENGTH_LONG).show();
    }

    private void addProduct() {
   
        String name = editTextName.getText().toString();
        double price = Double.parseDouble(editTextPrice.getText().toString());
    
      
        if (!TextUtils.isEmpty(name)) {
    
           
            String id = databaseProducts.push().getKey();
    
            
            Product product = new Product(id, name, price);
    
           
            databaseProducts.child(id).setValue(product);
    
          
            editTextName.setText("");
            editTextPrice.setText("");
    
            
            Toast.makeText(this, "Product added", Toast.LENGTH_LONG).show();
        } else {
     
            Toast.makeText(this, "Please enter a name", Toast.LENGTH_LONG).show();
        }
    }
}
