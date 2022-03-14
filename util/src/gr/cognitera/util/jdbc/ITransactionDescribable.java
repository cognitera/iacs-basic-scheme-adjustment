package gr.cognitera.util.jdbc;


public interface ITransactionDescribable {


    TransactionTypology getTypology();
    String getDescription();

}
