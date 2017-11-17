package effective2.Chapter10.Item67;
// Set obeserver callback interface - Page 266

public interface SetObserver<E> {
    // Invoked when an element is added to the observable set
    void added(ObservableSet<E> set, E element);
}
