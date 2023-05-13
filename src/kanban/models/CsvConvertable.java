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
     * @return Объект
     */
    void fromScsString(String csvString);
}
