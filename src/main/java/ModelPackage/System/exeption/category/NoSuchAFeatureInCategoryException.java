package ModelPackage.System.exeption.category;

public class NoSuchAFeatureInCategoryException extends RuntimeException {
    public NoSuchAFeatureInCategoryException(String feature,String categoryId){
        super(String.format("Feature (%s) Doesn't Exist in Category With Id (%s)",feature,categoryId));
    }
}
