package kanban.models;

public interface CsvConvertable {
    /**
     * Конвертация объекта в csv-строку
     * @return csv-строка
     */
    String toCsvString();

    /**
     * Сериализация объекта из csv-строки
     * @param csvString csv-строка
     */
    void fromScsString(String csvString);
}
